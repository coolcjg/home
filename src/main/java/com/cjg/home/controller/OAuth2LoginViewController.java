package com.cjg.home.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Log4j2
@Controller
public class OAuth2LoginViewController {

    @Value("${naver.callback-url}")
    private String callbackUrl;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @GetMapping("/login/oauth2/code/naver")
    public String naverLoginProcess(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        System.out.println("call /naverLoginProcess");

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

                String accessToken = jsonNode.get("access_token").asText();
                String refreshToken = jsonNode.get("refresh_token").asText();

                log.debug("accessToken : " + accessToken);
                log.debug("refreshToken : " + refreshToken);

                /** access token으로 상요자 정보 요청 후 응답 받기 **/
                JsonNode naverUserInfo = getUserInfo(accessToken);
                //id라는 애플리케이션 당 유니크한 일련번호값을 이용하여 자체적으로 회원정보를 구성필요.
                model.addAttribute("id", naverUserInfo.get("id").asText());
                model.addAttribute("nickname", naverUserInfo.get("nickname").asText());
                model.addAttribute("name", naverUserInfo.get("name").asText());
                model.addAttribute("email", naverUserInfo.get("email").asText());
                model.addAttribute("gender", naverUserInfo.get("gender").asText());
                model.addAttribute("age", naverUserInfo.get("age").asText());
                model.addAttribute("birthday", naverUserInfo.get("birthday").asText());
                model.addAttribute("profile_image", naverUserInfo.get("profile_image").asText());
                model.addAttribute("birthyear", naverUserInfo.get("birthyear").asText());
                model.addAttribute("mobile", naverUserInfo.get("mobile").asText());
            }

        }catch(Exception e){
            log.error("naver login error", e);
        }

        return "main";
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
