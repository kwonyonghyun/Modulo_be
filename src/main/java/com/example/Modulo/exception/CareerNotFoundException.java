package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class CareerNotFoundException extends BusinessException {
    public CareerNotFoundException() {
        super(ErrorCode.CAREER_NOT_FOUND);
    }
}