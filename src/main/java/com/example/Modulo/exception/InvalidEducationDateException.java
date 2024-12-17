package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEducationDateException extends BusinessException {
    public InvalidEducationDateException() {
        super(ErrorCode.INVALID_EDUCATION_DATE);
    }
}
