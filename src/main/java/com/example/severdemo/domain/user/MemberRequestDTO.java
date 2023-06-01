package com.example.severdemo.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {

    private String userId;
    private String username;
    private String password;
    private String nickname;
    private int gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birth;
    private String email;
    private String phoneNo;
    private String zipcode;
    private String street;
    private String addressDetail;
    private MultipartFile memberImage;
}