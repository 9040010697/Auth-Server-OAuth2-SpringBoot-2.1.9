package com.auth.controller;

import com.auth.controller.dto.CredDetails;
import com.auth.controller.dto.TokenDetails;
import com.auth.utils.CommonUtils;
import com.auth.utils.GrantType;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Base64;

@RestController
@RequestMapping("/auth-service")
@CrossOrigin
@Api(tags = "Auth-Service", description = "Auth-Service")
@Validated
public class TokenController {

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private ResourceServerTokenServices tokenServices;


    @PostMapping("/token")
    public TokenDetails generateToken(@RequestHeader(name = "x-auth-client-secret", defaultValue = "Basic dGVzdDp0ZW1w") String clientSecret,
                                      @RequestBody CredDetails credDetails) {
        return commonUtils.generateToken(credDetails);
    }

    @GetMapping("/refresh-token")
    public TokenDetails generateRefreshToken(@RequestHeader(name = "x-auth-client-secret", defaultValue = "Basic dGVzdDp0ZW1w") String clientSecret,
                                             @RequestParam String refresh_token,
                                             @RequestParam GrantType grant_type) {
        return commonUtils.generateRefreshToken(refresh_token, grant_type);
    }

    @DeleteMapping("/revoke")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String revokeAccessToken(@RequestHeader(name = "x-auth-client-secret", defaultValue = "Basic dGVzdDp0ZW1w") String clientSecret,
                                    @RequestParam String access_token) {
        return commonUtils.revokeToken(access_token);
    }


    @GetMapping(value = "/secret")
    @ResponseStatus(HttpStatus.OK)
    public String getBasicAuthToken(@RequestParam(defaultValue = "test") String clientId, @RequestParam(defaultValue = "temp") String clientSecret) {
        byte[] encode = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
        return "Basic " + new String(encode);
    }

    @GetMapping(value = "/encode")
    @ResponseStatus(HttpStatus.OK)
    public String getEncodedPassword(@RequestParam String rawPassword) {
        return commonUtils.getEncodedPassword(rawPassword);
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public UserDetails validate(
            @RequestHeader(name = "x-auth-client-secret", defaultValue = "Basic dGVzdDp0ZW1w")
                    String clientSecret, @RequestParam String token) {
        return commonUtils.validateToken(token);
    }
}
