package com.cjg.home.service;

import com.cjg.home.code.ResultCode;
import com.cjg.home.code.SocialType;
import com.cjg.home.code.UserRole;
import com.cjg.home.config.jwt.JwtTokenProvider;
import com.cjg.home.domain.User;
import com.cjg.home.dto.response.UserLoginResponseDto;
import com.cjg.home.exception.CustomException;
import com.cjg.home.repository.UserRepository;
import com.cjg.home.util.AES256;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.HashMap;
import java.util.Map;

import static com.cjg.home.domain.QUser.user;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuth2LoginService {

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.client-secret}")
    private String naverClientSecret;

    @Value("${naver.callback-url}")
    private String naverCallbackUrl;

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${kakao.callback-url}")
    private String kakaoCallbackUrl;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.callback-url}")
    private String googleCallbackUrl;

    private final UserRepository userRepository;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwt;

    private final AES256 aes256;

    private final PasswordEncoder passwordEncoder;

    public String loginUrl(HttpSession session, SocialType socialType) throws UnsupportedEncodingException {

        if(socialType.equals(SocialType.NAVER)){
            return naverLoginUrl(session);
        }else if(socialType.equals(SocialType.KAKAO)){
            return kakaoLoginUrl();
        }else if(socialType.equals(SocialType.GOOGLE)){
            return googleLoginUrl();
        }

        return "";
    }

    private String naverLoginUrl(HttpSession session) throws UnsupportedEncodingException {
        String redirectURI = URLEncoder.encode(naverCallbackUrl, "UTF-8");
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + naverClientId
                + "&redirect_uri=" + redirectURI
                + "&state=" + state;

        session.setAttribute("state", state);

        return apiURL;
    }

    private String kakaoLoginUrl(){
        String apiURL = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoRestApiKey
                + "&redirect_uri=" + kakaoCallbackUrl;
        return apiURL;
    };

    private String googleLoginUrl(){
        return "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=email profile"
                + "&client_id=" + googleClientId
                + "&redirect_uri=" + googleCallbackUrl;
    }

    public UserLoginResponseDto naverLoginProcess(HttpServletRequest request) throws UnsupportedEncodingException{

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectURI = URLEncoder.encode(naverCallbackUrl, "UTF-8");

        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
                + "&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
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

                /* DB에 사용자 정보 저장 or 업데이트*/
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
                }else{
                    user.setImage(profile_image);
                    user = userRepository.save(user);
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

    public UserLoginResponseDto kakaoLoginProcess(HttpServletRequest request,String code) {
        //1. 인가 코드 받기(String code)

        //2. accessToken 받기
        String kakaoAccessToken = getAccessTokenKakao(code);

        //3. 사용자 정보 받기
        User userInfo = getUserInfoKakao(kakaoAccessToken);

        /* DB에 사용자 정보 저장 or 업데이트*/
        User user = userRepository.findByUserId(userInfo.getUserId());
        if(user == null){
            User newUser = User.builder()
                    .userId(userInfo.getUserId())
                    .password(passwordEncoder.encode("socialLogin"))
                    .auth(UserRole.ADMIN.getValue())
                    .image(userInfo.getImage())
                    .name(aes256.encrypt(userInfo.getName()))
                    .socialType(SocialType.KAKAO)
                    .build();
            user = userRepository.save(newUser);
        }else{
            user.setImage(userInfo.getImage());
            user = userRepository.save(user);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserId());

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

    public String getAccessTokenKakao(String code){

        String accessToken = "";
        String refreshToken = "";
        String regUrl = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(regUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoRestApiKey);
            sb.append("&client_secret=").append(kakaoClientSecret);
            sb.append("&redirect_uri=").append(kakaoCallbackUrl);
            sb.append("&code=").append(code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[kakaoApi getAccessToen] responseCode = {}", responseCode);

            BufferedReader br;
            if(responseCode >= 200 && responseCode < 300){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder responseSb = new StringBuilder();

            while((line = br.readLine()) != null){
                responseSb.append(line);
            }

            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

        } catch (IOException e) {
            throw new CustomException(ResultCode.OAUTH_LOGIN_EXCEPTION);
        }

        return accessToken;
    }

    private User getUserInfoKakao(String accessToken){
        User user;
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try{
            URL url= new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            log.info("[kakao getUserInfo] responseCode : {}", responseCode);

            BufferedReader br;
            if(responseCode >= 200 && responseCode < 300){
                br = new  BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                br = new  BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String profile_image = properties.getAsJsonObject().get("profile_image").getAsString();
            String email = kakaoAccount.getAsJsonObject().get("email").getAsString();

            user = User.builder()
                        .userId(email)
                        .name(nickname)
                        .image(profile_image).build();

            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user;
    }


    public UserLoginResponseDto googleLoginProcess(HttpServletRequest request,String code) {

        //1. 인가 코드 받기(String code)

        //2. accessToken 받기
        String googleAccessToken = getAccessTokenGoogle(code);

        //3. 사용자 정보 받기
        User userInfo = getUserInfoGoogle(googleAccessToken);

        /* DB에 사용자 정보 저장 or 업데이트*/
        User user = userRepository.findByUserId(userInfo.getUserId());
        if(user == null){
            User newUser = User.builder()
                    .userId(userInfo.getUserId())
                    .password(passwordEncoder.encode("socialLogin"))
                    .auth(UserRole.ADMIN.getValue())
                    .image(userInfo.getImage())
                    .name(aes256.encrypt(userInfo.getName()))
                    .socialType(SocialType.GOOGLE)
                    .build();
            user = userRepository.save(newUser);
        }else{
            user.setImage(userInfo.getImage());
            user = userRepository.save(user);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserId());

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

    private User getUserInfoGoogle(String accessToken){
        String reqUrl = "https://www.googleapis.com/userinfo/v2/me";
        User user;
        try{
            URL url= new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            log.info("[google getUserInfo] responseCode : {}", responseCode);

            BufferedReader br;
            if(responseCode >= 200 && responseCode < 300){
                br = new  BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                br = new  BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String id = element.getAsJsonObject().get("id").getAsString();
            String email = element.getAsJsonObject().get("email").getAsString();
            String name = element.getAsJsonObject().get("name").getAsString();
            String picture = element.getAsJsonObject().get("picture").getAsString();

            user = User.builder()
                        .userId(email)
                        .name(name)
                        .image(picture).build();

            br.close();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public String getAccessTokenGoogle(String code){

        String accessToken = "";
        String refreshToken = "";
        String regUrl = "https://oauth2.googleapis.com/token";

        try{
            URL url = new URL(regUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(googleClientId);
            sb.append("&client_secret=").append(googleClientSecret);
            sb.append("&redirect_uri=").append(googleCallbackUrl);
            sb.append("&code=").append(code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[googleApi getAccessToen] responseCode = {}", responseCode);

            BufferedReader br;
            if(responseCode >= 200 && responseCode < 300){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder responseSb = new StringBuilder();

            while((line = br.readLine()) != null){
                responseSb.append(line);
            }

            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();

            br.close();
            bw.close();

        } catch (IOException e) {
            throw new CustomException(ResultCode.OAUTH_LOGIN_EXCEPTION);
        }

        return accessToken;
    }








}
