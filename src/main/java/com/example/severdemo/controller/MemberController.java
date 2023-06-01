package com.example.severdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.severdemo.domain.token.TokenDTO;
import com.example.severdemo.domain.token.TokenResponseDTO;
import com.example.severdemo.domain.user.LoginRequestDTO;
import com.example.severdemo.domain.user.MemberRequestDTO;
import com.example.severdemo.domain.user.MemberDTO;
import com.example.severdemo.security.SecurityUtil;
import com.example.severdemo.service.MemberService;

@Tag(name = "사용자 관련", description = "사용자 관련 API")
@RestController
@Log4j2
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> memberSignup(@ModelAttribute MemberRequestDTO memberRequestDTO) {
        memberRequestDTO.setPassword(passwordEncoder.encode(memberRequestDTO.getPassword()));
        memberService.signup(memberRequestDTO);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    //social여부 체크해야함
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> memberLogin(@ModelAttribute LoginRequestDTO loginRequestDTO) {
        log.info(loginRequestDTO);
        TokenDTO tokenDTO = memberService.login(loginRequestDTO);
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

        return ResponseEntity.ok().header("Set-Cookie", responseCookie.toString()).body(tokenResponseDTO);
    }

    @Operation(summary = "회원정보 호출")
    @GetMapping("/getMemberData")
    public ResponseEntity<MemberDTO> loadMemberData() {
        return ResponseEntity.ok(memberService.getMember(SecurityUtil.getCurrentUsername()));
    }

    @Operation(summary = "회원정보 수정")
    @PostMapping("/modify")
    public ResponseEntity memberModify(@ModelAttribute MemberRequestDTO memberRequestDTO) {
        if(!SecurityUtil.getCurrentUsername().equals(memberRequestDTO.getUserId())) {
            log.warn("잘못된 회원 ID로 접근하였습니다.");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        memberRequestDTO.setPassword(passwordEncoder.encode(memberRequestDTO.getPassword()));
        memberService.updateMember(memberRequestDTO, SecurityUtil.getCurrentUsername());
        return new ResponseEntity(HttpStatus.OK);
    }
}