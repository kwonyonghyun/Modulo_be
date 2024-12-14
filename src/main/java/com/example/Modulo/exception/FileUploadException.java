package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class FileUploadException extends BusinessException {
    public FileUploadException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }
}