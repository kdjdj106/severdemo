package com.example.severdemo.domain.auth;

import com.example.severdemo.domain.type.SocialType;
import com.example.severdemo.domain.user.Member;
import com.example.severdemo.domain.user.userInfo.*;
import com.example.severdemo.domain.user.userInfo.KakaoOAuth2UserInfo;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Builder
@Slf4j
public class OAuth2Attribute {

    private String provider;
    private String userId;
    private String username;
    private String email;
    private String picture;
    private String nickname;
    private String image;


    ////
    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

//    @Builder
//    public OAuth2Attribute(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
//        this.nameAttributeKey = nameAttributeKey;
//        this.oauth2UserInfo = oauth2UserInfo;
//    }


    public static OAuth2Attribute of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attribute ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .provider(userNameAttributeName)
                .username(String.valueOf(attributes.get("username")))
                .email(String.valueOf(attributes.get("email")))
                .userId(String.valueOf(attributes.get("id")))
                .image(String.valueOf(attributes.get("profileimage")))
                .build();
    }

    public static OAuth2Attribute ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuth2Attribute ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

//    public Member toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
//
//        return Member.builder()
//                .(socialType)
//                .socialId(oauth2UserInfo.getId())
//                .email(oauth2UserInfo.getEmail())
//                .nickname(oauth2UserInfo.getNickname())
//                .imageUrl(oauth2UserInfo.getImageUrl())
//                .usercode(socialType+oauth2UserInfo.getId())
//                .role(Role.GUEST)
//                .build();
//    }

    ////
    public static OAuth2Attribute of(String provider, String usernameAttributeName, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return OAuth2Attribute.ofGoogle(provider, usernameAttributeName, attributes);
            case "kakao":
                log.info("kakao 케이스 실행");
                return OAuth2Attribute.ofKakao(usernameAttributeName, attributes);
            default:
                throw new RuntimeException("소셜 로그인 접근 실패");
        }

    }

    private static OAuth2Attribute ofGoogle(String provider, String usernameAttributeName, Map<String, Object> attributes) {

        return OAuth2Attribute.builder()
                .provider(provider)
                .username(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .userId(String.valueOf(attributes.get(usernameAttributeName)).concat("google"))
                .build();
    }
    private static OAuth2Attribute ofKakao(String provider, String usernameAttributeName, Map<String, Object> attributes) {

        return OAuth2Attribute.builder()
                .provider(provider)
                .username(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .picture(String.valueOf(attributes.get("profile_image")))
                .userId(String.valueOf(attributes.get(usernameAttributeName)).concat("google"))
                .build();
    }
}
