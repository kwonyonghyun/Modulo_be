package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidCareerDateException extends BusinessException {
    public InvalidCareerDateException() {
        super(ErrorCode.INVALID_CAREER_DATE);
    }
}