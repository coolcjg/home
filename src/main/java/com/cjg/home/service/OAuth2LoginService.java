package com.cjg.home.service;

import com.cjg.home.code.SocialType;
import com.cjg.home.code.UserRole;
import com.cjg.home.config.jwt.JwtTokenProvider;
import com.cjg.home.domain.User;
import com.cjg.home.dto.response.UserLoginResponseDto;
import com.cjg.home.repository.UserRepository;
import com.cjg.home.util.AES256;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuth2LoginService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.callback-url}")
    private String callbackUrl;

    private final UserRepository userRepository;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwt;

    private final AES256 aes256;

    private final PasswordEncoder passwordEncoder;

    public String getNaverApiUrl(HttpSession session) throws UnsupportedEncodingException {

        String redirectURI = URLEncoder.encode(callbackUrl, "UTF-8");
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectURI
                + "&state=" + state;

        session.setAttribute("state", state);

        return apiURL;
    }


    public UserLoginResponseDto naverLoginProcess(HttpServletRequest request) throws UnsupportedEncodingException{

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode(callbackUrl, "UTF-8");

        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectURI
                + "&code=" + code
                + "&state=" + state;

        try{
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            System.out.println("responseCode = " + responseCode);
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder res = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                res.append(inputLine);
            }

            br.close();

            // access token 요청에 대한 응답을 정상적으로 받았다면,
            if(responseCode == 200){

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(res.toString());
                log.debug(jsonNode.toString());

                String accessToken_naver = jsonNode.get("access_token").asText();
                String refreshToken_naver = jsonNode.get("refresh_token").asText();

                log.info("accessToken : " + accessToken_naver);
                log.info("refreshToken : " + refreshToken_naver);

                /** access token으로 상요자 정보 요청 후 응답 받기 **/
                JsonNode naverUserInfo = getUserInfo(accessToken_naver);

                String id = naverUserInfo.get("id").asText();
                String nickname = naverUserInfo.get("name").asText();
                String name = naverUserInfo.get("name").asText();
                String email =  naverUserInfo.get("email").asText();
                String gender = naverUserInfo.get("gender").asText();

                String age = naverUserInfo.get("age").asText();
                String birthday = naverUserInfo.get("birthday").asText();
                String profile_image = naverUserInfo.get("profile_image").asText();
                String birthyear = naverUserInfo.get("birthyear").asText();
                String mobile = naverUserInfo.get("mobile").asText();

                /* DB에 사용자 정보가 없으며 저장한다.*/
                User user = userRepository.findByUserId(id);
                if(user == null){
                    User newUser = User.builder()
                            .userId(id)
                            .password(passwordEncoder.encode("socialLogin"))
                            .auth(UserRole.ADMIN.getValue())
                            .image(profile_image)
                            .name(aes256.encrypt(name))
                            .socialType(SocialType.NAVER)
                            .build();

                    user = userRepository.save(newUser);
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(id);

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, "socialLogin", userDetails.getAuthorities());
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
           }

        } catch(IOException e) {
            log.error("naver login error", e);
        }

        return null;
    }


    public JsonNode getUserInfo(String token){

        try{
            String url = "https://openapi.naver.com/v1/nid/me";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = con.getResponseCode();
            log.debug("ResponseCode : " +  responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK){
                String res = readBody(con.getInputStream());

                log.debug("userinfo = " + res);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(res.toString());
                final String SUCCESS_CODE = "00";
                final String SUCCESS_MESSAGE = "success";
                if(SUCCESS_CODE.equals(jsonNode.get("resultcode").asText())
                        && SUCCESS_MESSAGE.equals(jsonNode.get("message").asText())){
                    return jsonNode.get("response");
                }
            }else{
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(readBody(con.getErrorStream()));
                return jsonNode;
            }

        }catch(Exception e){
            e.printStackTrace();

        }

        return null;

    }

    private String readBody(InputStream body)  {
        InputStreamReader streamReader = new InputStreamReader(body);

        try(BufferedReader lineReader = new BufferedReader(streamReader)){
            StringBuilder responseBody = new StringBuilder();

            String line;
            while((line = lineReader.readLine()) != null){
                responseBody.append(line);
            }

            return responseBody.toString();

        }catch(IOException e){
            throw new RuntimeException("API 응답을 읽는데 실패");
        }
    }



}
