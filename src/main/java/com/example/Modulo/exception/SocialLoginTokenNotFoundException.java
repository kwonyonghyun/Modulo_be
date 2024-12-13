package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class SocialLoginTokenNotFoundException extends BusinessException {

    public SocialLoginTokenNotFoundException() {
        super(ErrorCode.SOCIAL_LOGIN_TOKEN_NOT_FOUND);
    }
}