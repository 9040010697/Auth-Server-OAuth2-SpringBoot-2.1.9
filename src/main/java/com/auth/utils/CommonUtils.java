package com.auth.utils;

import com.auth.controller.dto.CredDetails;
import com.auth.controller.dto.TokenDetails;
import com.auth.entity.AppUser;
import com.auth.repository.UserRepository;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Component
public class CommonUtils {

    @Value("${security.signing-key}")
    private String signingKey;

    @Autowired
    private UserRepository repo;

    private RestTemplate restTemplate;

    public CommonUtils(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Autowired
    private JdbcTokenStore tokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TokenDetails generateToken(CredDetails credDetails) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/oauth/token");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("grant_type", credDetails.getGrant_type().getGrantType());
        map.set("username", credDetails.getUsername());
        map.set("password", credDetails.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = getBasicAuth();
        headers.set(HttpHeaders.AUTHORIZATION, auth);
        HttpEntity<?> entity = new HttpEntity(map, headers);

        return restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST, entity, TokenDetails.class)
                .getBody();

    }

    public TokenDetails generateRefreshToken(String refresh_token, GrantType grant_type) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/oauth/token");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("grant_type", grant_type.getGrantType());
        map.set("refresh_token", refresh_token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = getBasicAuth();
        headers.set(HttpHeaders.AUTHORIZATION, auth);
        HttpEntity<?> entity = new HttpEntity(map, headers);

        return restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST, entity, TokenDetails.class)
                .getBody();
    }

    public String revokeToken(String accessToken) {
        tokenStore.removeAccessToken(accessToken);
        return "Revoked successfully!!";
    }

    public String getBasicAuthToken(@RequestParam String clientId, @RequestParam String clientSecret) {
        byte[] encode = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
        return "Basic " + new String(encode);
    }

    public String getEncodedPassword(@RequestParam String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public static String getBasicAuth() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        return request.getHeader("x-auth-client-secret");
    }

    public UserDetails validateToken(String accessToken) {
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);

        return validate(oAuth2AccessToken.toString());
    }


    @SneakyThrows
    public UserDetails validate(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = token  .split("\\."); // split out the "parts" (header, payload and signature)
        //String headerJson = new String(decoder.decode(parts[0]));
        String payloadJson = new String(decoder.decode(parts[1]));
        //String signatureJson = new String(decoder.decode(parts[2]));
        HashMap<String, Object> values = new ObjectMapper().readValue(payloadJson.getBytes(), HashMap.class);

        try {
            AppUser user = repo.findByUsername((String) values.get("user_name"));
            if (user != null) {
                return getUserDetails(repo, user.getUsername());
            } else {
                throw new RuntimeException("User details are not valid");
            }
        } catch (Exception e) {
            throw new RuntimeException("JWT token is not valid");
        }
    }

    public UserDetails getUserDetails(UserRepository userRepository, String userName) {
        AppUser user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", userName));
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
