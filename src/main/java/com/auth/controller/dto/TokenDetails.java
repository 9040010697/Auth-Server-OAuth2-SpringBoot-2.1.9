package com.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetails {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String scope;

}
