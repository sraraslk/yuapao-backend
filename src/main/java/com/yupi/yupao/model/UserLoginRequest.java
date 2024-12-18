package com.yupi.yupao.model;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String userAccount;
    private String userPassword;
}
