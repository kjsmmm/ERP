package com.erp.common.utils;

import com.erp.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtils {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${upload.base-path:uploads}")
    private String basePath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL（相对路径）
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 校验文件类型（MIME + 扩展名双重校验）
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件类型，仅支持 jpg/png/gif/webp");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new BusinessException("不支持的文件扩展名，仅支持 jpg/png/gif/webp");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 生成文件路径：uploads/product/20260520/uuid.ext
        String ext = getExtension(file.getOriginalFilename());
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String relativePath = "product/" + dateStr + "/" + fileName;

        Path targetPath = Paths.get(basePath, relativePath);
        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        return urlPrefix + "/" + relativePath;
    }

    /**
     * 删除文件
     *
     * @param url 文件URL（相对路径）
     */
    public void delete(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }
        // 从URL中提取相对路径
        String relativePath = url.replace(urlPrefix + "/", "");
        Path filePath = Paths.get(basePath, relativePath).normalize();
        Path baseDir = Paths.get(basePath).normalize();
        // 路径穿越校验：删除目标必须在basePath下
        if (!filePath.startsWith(baseDir)) {
            throw new BusinessException("文件路径不合法");
        }
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // 文件删除失败不影响业务
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1) : "jpg";
    }
}
