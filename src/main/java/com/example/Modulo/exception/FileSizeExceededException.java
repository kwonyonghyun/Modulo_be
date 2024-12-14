package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class FileSizeExceededException extends BusinessException {
    public FileSizeExceededException() {
        super(ErrorCode.FILE_SIZE_EXCEED);
    }
}