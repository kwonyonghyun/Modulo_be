package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class BasicInfoDuplicateException extends BusinessException {
    public BasicInfoDuplicateException() {
        super(ErrorCode.BASIC_INFO_DUPLICATE);
    }
}