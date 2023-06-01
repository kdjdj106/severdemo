package com.example.severdemo.domain.user.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickname() {

        return   String.valueOf(attributes.get("nickname"));
    }

    @Override
    public String getImageUrl() {
        return   String.valueOf(attributes.get("profileimage"));

    }

    @Override
    public String getEmail() {
        return   String.valueOf(attributes.get("email"));
    }

}
