package com.example.severdemo.controller;
import com.example.severdemo.service.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  com.example.severdemo.domain.token.TokenDTO;
import  com.example.severdemo.domain.token.TokenResponseDTO;
import com.example.severdemo.domain.user.MemberDTO;
import com.example.severdemo.service.OAuth2UserService;
import com.example.severdemo.service.TokenService;

import java.util.HashMap;
import java.util.Map;
@RestController
@Log4j2
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final TokenService tokenService;
    private final OAuth2UserService oAuth2UserService;
    private final TestService testService;

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refreshToken")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(tokenService.refresh(tokenDTO));
    }

    @Operation(summary = "구글 소셜 로그인")
    @GetMapping("/oauth2/google")
    public ResponseEntity<TokenResponseDTO> oauth2Google(@RequestParam("id_token") String idToken) throws ParseException, JsonProcessingException {
        Map<String, Object> memberMap =  oAuth2UserService.findOrSaveMember(idToken, "google");
        TokenDTO tokenDTO = tokenService.createToken((MemberDTO) memberMap.get("dto"));

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(tokenDTO.getDuration())
                .path("/")
                .build();

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .isNewMember(false)
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.status((Integer) memberMap.get("status")).header("Set-Cookie", responseCookie.toString()).body(tokenResponseDTO);
    }

    @Operation(summary = "카카오 소셜 로그인")
    @GetMapping("/oauth2/kakao")
    public ResponseEntity<TokenResponseDTO> oauth2Kakao(@RequestParam("code") String code) throws ParseException, JsonProcessingException {
        System.out.println("카카오 소셜 로그인");
        String accessToken = testService.getAccessToken(code);


        Map<String, Object> userInfo = testService.getUserInfo(accessToken);

        Map<String, Object> memberMap =  oAuth2UserService.findOrSaveMember(accessToken, "kakao");
        TokenDTO tokenDTO = tokenService.createToken((MemberDTO) memberMap.get("dto"));

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", tokenDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(tokenDTO.getDuration())
                .path("/")
                .build();

        TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder()
                .isNewMember(false)
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.status((Integer) memberMap.get("status")).header("Set-Cookie", responseCookie.toString()).body(tokenResponseDTO);
    }
    @GetMapping("/test")
    public String test(){
        System.out.println("test");
        return "test";
    }
    @ResponseBody
    @GetMapping("/kakao")
    public String login(@RequestParam("code") String code) {
        System.out.println("==========");
        System.out.println(code);
        try {
            String accessToken = testService.getAccessToken(code);


            Map<String, Object> userInfo = testService.getUserInfo(accessToken);
            System.out.println("login Controller : " + userInfo);

//            Map<String, Object> userinfo =
        }catch (RuntimeException e){
            System.out.println(e.toString());
        }

        return "index";
    }

}
