package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.workflow.WorkflowService;
import com.erp.purchase.dto.PurchaseRequestDTO;
import com.erp.purchase.entity.PurchaseRequest;
import com.erp.purchase.entity.PurchaseRequestItem;
import com.erp.purchase.mapper.PurchaseRequestItemMapper;
import com.erp.purchase.mapper.PurchaseRequestMapper;
import com.erp.purchase.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl extends ServiceImpl<PurchaseRequestMapper, PurchaseRequest> implements PurchaseRequestService {

    private final PurchaseRequestItemMapper purchaseRequestItemMapper;
    private final WorkflowService workflowService;

    @Override
    public IPage<PurchaseRequest> getPurchaseRequestPage(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<PurchaseRequest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseRequest> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PurchaseRequest::getRequestNo, keyword);
        }
        if (status != null) {
            wrapper.eq(PurchaseRequest::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseRequest::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void createPurchaseRequest(PurchaseRequestDTO dto) {
        PurchaseRequest request = new PurchaseRequest();
        BeanUtils.copyProperties(dto, request);
        request.setRequestNo(generateRequestNo());
        request.setStatus(0); // 草稿
        save(request);

        // 保存明细
        for (PurchaseRequestDTO.PurchaseRequestItemDTO itemDTO : dto.getItems()) {
            PurchaseRequestItem item = new PurchaseRequestItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setPurchaseRequestId(request.getId());
            purchaseRequestItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void submitForApproval(Long id) {
        PurchaseRequest request = getById(id);
        if (request == null) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "采购申请不存在");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_STATUS_ERROR, "只有草稿状态可以提交审批");
        }

        String processInstanceId = workflowService.startProcess(
                "purchase-request-approval",
                String.valueOf(id),
                String.valueOf(getCurrentUserId()),
                null
        );

        request.setProcessInstanceId(processInstanceId);
        request.setStatus(1); // 审批中
        updateById(request);
    }

    @Override
    @Transactional
    public void approveCallback(String processInstanceId, boolean approved) {
        PurchaseRequest request = getOne(new LambdaQueryWrapper<PurchaseRequest>()
                .eq(PurchaseRequest::getProcessInstanceId, processInstanceId));
        if (request == null) {
            return;
        }
        request.setStatus(approved ? 2 : 3); // 2=已通过, 3=已驳回
        updateById(request);
    }

    private String generateRequestNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PR-" + dateStr + "-";
        PurchaseRequest maxRequest = getOne(new LambdaQueryWrapper<PurchaseRequest>()
                .likeRight(PurchaseRequest::getRequestNo, prefix)
                .orderByDesc(PurchaseRequest::getRequestNo)
                .last("LIMIT 1"));
        int seq = 1;
        if (maxRequest != null && maxRequest.getRequestNo() != null && maxRequest.getRequestNo().startsWith(prefix)) {
            try {
                seq = Integer.parseInt(maxRequest.getRequestNo().substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("PR-%s-%03d", dateStr, seq);
    }

    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L;
    }
}
