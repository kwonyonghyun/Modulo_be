package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class SocialLoginUserInfoNotFoundException extends BusinessException {

    public SocialLoginUserInfoNotFoundException() {
        super(ErrorCode.SOCIAL_LOGIN_USER_INFO_NOT_FOUND);
    }
}