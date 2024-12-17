package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidCareerFieldException extends BusinessException {

    public InvalidCareerFieldException() {
        super(ErrorCode.INVALID_CAREER_FIELD);
    }
}