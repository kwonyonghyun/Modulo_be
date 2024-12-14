package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class EducationNotFoundException extends BusinessException {
    public EducationNotFoundException() {
        super(ErrorCode.EDUCATION_NOT_FOUND);
    }
}