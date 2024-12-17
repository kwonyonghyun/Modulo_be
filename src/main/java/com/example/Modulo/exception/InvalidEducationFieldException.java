package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEducationFieldException extends BusinessException {
    public InvalidEducationFieldException() {
        super(ErrorCode.INVALID_EDUCATION_FIELD);
    }
}