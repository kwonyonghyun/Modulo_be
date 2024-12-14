package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class BasicInfoUnauthorizedException extends BusinessException {
    public BasicInfoUnauthorizedException() {
        super(ErrorCode.BASIC_INFO_UNAUTHORIZED);
    }
}