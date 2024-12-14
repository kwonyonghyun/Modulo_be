package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class BasicInfoNotFoundException extends BusinessException {
    public BasicInfoNotFoundException() {
        super(ErrorCode.BASIC_INFO_NOT_FOUND);
    }
}