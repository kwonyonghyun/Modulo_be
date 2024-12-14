package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class FileNotFoundException extends BusinessException {
    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }
}