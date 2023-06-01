package com.example.severdemo.domain.user;

import com.example.severdemo.domain.type.SocialType;
import jakarta.persistence.*;

import com.example.severdemo.domain.auth.Provider;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Member implements Persistable<String> {

    @Id
    @NonNull
    @Column(updatable = false, unique = true)
    private String userId;     //사용자 ID값
    private String password;
    private String username;
    private String nickname;
    private int gender;   //0기타 1남성 2여성
    private LocalDate birth;
    @NonNull
    @Column(unique = true)
    private String email;
    private String phoneNo;
    private boolean social;
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String zipcode;
    private String street;
    private String addressDetail;

    @Enumerated(EnumType.STRING)
    private List<Role> roles;
    @OneToOne(fetch = FetchType.LAZY)
    private MemberImage memberImage;    //프로필 사진

    @CreatedDate
    @Column(updatable = false)
    @NonNull
    private LocalDateTime createdDate;

    @Builder
    public Member(@NonNull String userId, String password, String username, String nickname, int gender, LocalDate birth, @NonNull String email, String phoneNo, boolean social, Provider provider, String zipcode, String street, String addressDetail, List<Role> roles, LocalDateTime createdDate) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.email = email;
        this.phoneNo = phoneNo;
        this.social = social;
        this.provider = provider;
        this.zipcode = zipcode;
        this.street = street;
        this.addressDetail = addressDetail;
        this.roles = roles;
        this.createdDate = createdDate;
    }

    public void updateMemberImage(MemberImage memberImage) {
        this.memberImage = memberImage;
    }

    public void updateRole(Role role) {
        if(this.roles == null) {
            this.roles = new ArrayList<>();
        }

        if(this.roles.contains(role)) {
            this.roles.remove(role);
        }
        else {
            this.roles.add(role);
        }
    }

    public void updateSocial(Provider provider) {
        this.social = true;
        this.provider = provider;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
