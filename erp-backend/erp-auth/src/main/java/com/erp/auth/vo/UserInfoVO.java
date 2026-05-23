package com.erp.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户信息VO
 */
@Data
@Builder
public class UserInfoVO {

    private Long id;
    private String username;
    private String nickname;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
