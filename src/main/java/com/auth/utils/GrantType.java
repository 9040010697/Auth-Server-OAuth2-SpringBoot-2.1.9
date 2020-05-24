package com.auth.utils;

import lombok.Getter;

@Getter
public enum GrantType {

    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token");

    private String grantType;

    GrantType(String grantType) {
        this.grantType = grantType;
    }
}
