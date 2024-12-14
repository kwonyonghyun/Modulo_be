package com.example.Modulo.global.enums;

public enum EtcType {
    CERTIFICATE("자격증"),
    ACTIVITY("대외활동"),
    LANGUAGE("어학"),
    AWARD("수상이력");

    private final String description;

    EtcType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 