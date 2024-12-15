package com.example.Modulo.exception;

import com.example.Modulo.global.enums.SectionType;
import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidSectionContentException extends BusinessException {
    public InvalidSectionContentException(SectionType sectionType, Long contentId) {
        super(ErrorCode.INVALID_SECTION_CONTENT);
    }
}