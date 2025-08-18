package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.code.UserRole;
import com.cjg.home.config.jwt.JwtTokenProvider;
import com.cjg.home.domain.User;
import com.cjg.home.dto.request.UserLoginRequestDto;
import com.cjg.home.dto.request.UserModifyRequestDto;
import com.cjg.home.dto.request.UserSaveRequestDto;
import com.cjg.home.dto.response.UserLoginResponseDto;
import com.cjg.home.dto.response.UserResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.UserRepository;
import com.cjg.home.util.AES256;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${storage.path}")
    private String storagePath;

    private final UserRepository userRepository;

    private final AES256 aes256;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwt;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisService redisService;

    public Long count(String userId){
        return userRepository.countByUserId(userId);
    }

    public UserResponseDto save(UserSaveRequestDto dto){

        User user = null;

        try{
            user = User.builder()
                    .userId(dto.getUserId())
                    .auth(UserRole.ADMIN.getValue())
                    .name(aes256.encrypt(dto.getName()))
                    .image(saveFile(dto.getImage()))
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();
        }catch(IOException e){
            throw new CustomException(ResultCode.IO_FAIL);
        }

        User result = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(result.getUserId())
                .name(result.getName())
                .image(result.getImage())
                .build();
    }


    public String saveFile(MultipartFile file) throws IOException {

        // 저장할 경로
        String uuid = UUID.randomUUID().toString();
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        File dest = new File(storagePath + uuid + ext);

        // 디렉토리가 없으면 생성
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 파일 저장
        file.transferTo(dest);

        return uuid + ext;
    }

    public UserLoginResponseDto login(UserLoginRequestDto requestDto){

        User user = userRepository.findByUserId(requestDto.getUserId());

        if(user == null){
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }else{

            if(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.getUserId());

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, requestDto.getPassword(), userDetails.getAuthorities());
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String accessToken = jwt.createAccessToken(authentication);
                String refreshToken = jwt.createRefreshToken(authentication);

                return UserLoginResponseDto.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

            }else {
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }

        }
    }

    public User findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }


    @Transactional
    public UserResponseDto modify(UserModifyRequestDto dto){

        User user = userRepository.findByUserId(dto.getUserId());
        user.setName(aes256.encrypt(dto.getName()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if(dto.getImage() != null){
            deleteFile(user.getImage());

            String newImage = null;
            try{
                newImage = saveFile(dto.getImage());
            }catch(IOException e){
                throw new CustomException(ResultCode.IO_FAIL);
            }

            user.setImage(newImage);

        }

        return UserResponseDto.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .image(user.getImage())
                .build();
    }

    public boolean deleteFile(String fileName){
        File file = new File(storagePath + fileName);
        return file.delete();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response){
        try{
            redisService.delete(jwtTokenProvider.getUserPrincipal(jwtTokenProvider.resolveToken(request)[0]));
        }catch(ExpiredJwtException e){
            log.error("logout invalid token");
        }
        jwtTokenProvider.removeTokenFromCookie(request, response);
        SecurityContextHolder.clearContext();
    }




}



