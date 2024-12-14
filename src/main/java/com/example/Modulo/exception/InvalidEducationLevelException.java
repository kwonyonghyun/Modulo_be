package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEducationLevelException extends BusinessException {

    public InvalidEducationLevelException() {
        super(ErrorCode.INVALID_EDUCATION_LEVEL);
    }
}