package com.auth.controller.dto;

import com.auth.utils.GrantType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CredDetails {

    @NotNull
    GrantType grant_type;

    @NotNull
    String username;

    @NotNull
    String password;


}
