package com.cjg.home.service;

import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.domain.User;
import com.cjg.home.repository.UserRepository;
import com.cjg.home.util.AES256;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private  final UserRepository userRepository;

    private final AES256 aes256;

    @Value("${image.url.prefix}")
    private String imageUrl;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);

        if(user.getImage() != null && !user.getImage().startsWith("http")){
            user.setImage(imageUrl+user.getImage());
        }

        return new CustomUserDetails(user.getUserId(), aes256.decrypt(user.getName()), user.getAuth(), user.getPassword(), user.getImage());
    }
}
