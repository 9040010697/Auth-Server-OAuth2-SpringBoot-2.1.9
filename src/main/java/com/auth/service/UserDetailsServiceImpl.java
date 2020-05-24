package com.auth.service;

import com.auth.repository.UserRepository;
import com.auth.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonUtils commonUtils;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // access token generation time
        return commonUtils.getUserDetails(userRepository, userName);
    }
}
