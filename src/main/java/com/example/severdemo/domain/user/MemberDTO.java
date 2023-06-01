package com.example.severdemo.domain.user;

import com.example.severdemo.domain.auth.Provider;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MemberDTO {

    private String userId;
    private String username;
    private String nickname;
    private int gender;
    private LocalDate birth;
    private String email;
    private String phoneNo;
    private String zipcode;
    private boolean social;
    private Provider provider;
    private String street;
    private String addressDetail;
    private MemberImage memberImage;
    private List<Role> roles;
    private LocalDateTime createdDate;

    @Builder
    public MemberDTO(String userId, String username, String nickname, int gender, LocalDate birth, String email, String phoneNo, String zipcode, boolean social, Provider provider, String street, String addressDetail, MemberImage memberImage, List<Role> roles, LocalDateTime createdDate) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.email = email;
        this.phoneNo = phoneNo;
        this.zipcode = zipcode;
        this.social = social;
        this.provider = provider;
        this.street = street;
        this.addressDetail = addressDetail;
        this.memberImage = memberImage;
        this.roles = roles;
        this.createdDate = createdDate;
    }
}