package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class FileDeleteException extends BusinessException {
    public FileDeleteException() {
        super(ErrorCode.FILE_DELETE_ERROR);
    }
}