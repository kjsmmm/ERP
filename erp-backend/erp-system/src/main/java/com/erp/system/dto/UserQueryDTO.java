package com.erp.system.dto;

import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * 用户查询 DTO
 */
@Data
public class UserQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 10;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;
}
