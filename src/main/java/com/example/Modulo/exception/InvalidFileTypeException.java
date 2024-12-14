package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidFileTypeException extends BusinessException {
    public InvalidFileTypeException() {
        super(ErrorCode.INVALID_FILE_TYPE);
    }
}