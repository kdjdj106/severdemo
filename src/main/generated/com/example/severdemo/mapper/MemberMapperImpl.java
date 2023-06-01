package com.example.severdemo.mapper;

import com.example.severdemo.domain.user.Member;
import com.example.severdemo.domain.user.MemberDTO;
import com.example.severdemo.domain.user.MemberImage;
import com.example.severdemo.domain.user.MemberRequestDTO;
import com.example.severdemo.domain.user.Role;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-01T18:30:28+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Amazon.com Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberDTO memberToMemberDTO(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberDTO.MemberDTOBuilder memberDTO = MemberDTO.builder();

        memberDTO.userId( member.getUserId() );
        memberDTO.username( member.getUsername() );
        memberDTO.nickname( member.getNickname() );
        memberDTO.gender( member.getGender() );
        memberDTO.birth( member.getBirth() );
        memberDTO.email( member.getEmail() );
        memberDTO.phoneNo( member.getPhoneNo() );
        memberDTO.zipcode( member.getZipcode() );
        memberDTO.social( member.isSocial() );
        memberDTO.provider( member.getProvider() );
        memberDTO.street( member.getStreet() );
        memberDTO.addressDetail( member.getAddressDetail() );
        memberDTO.memberImage( member.getMemberImage() );
        List<Role> list = member.getRoles();
        if ( list != null ) {
            memberDTO.roles( new ArrayList<Role>( list ) );
        }
        memberDTO.createdDate( member.getCreatedDate() );

        return memberDTO.build();
    }

    @Override
    public Member memberDTOToMember(MemberDTO memberDTO) {
        if ( memberDTO == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.userId( memberDTO.getUserId() );
        member.username( memberDTO.getUsername() );
        member.nickname( memberDTO.getNickname() );
        member.gender( memberDTO.getGender() );
        member.birth( memberDTO.getBirth() );
        member.email( memberDTO.getEmail() );
        member.phoneNo( memberDTO.getPhoneNo() );
        member.social( memberDTO.isSocial() );
        member.provider( memberDTO.getProvider() );
        member.zipcode( memberDTO.getZipcode() );
        member.street( memberDTO.getStreet() );
        member.addressDetail( memberDTO.getAddressDetail() );
        List<Role> list = memberDTO.getRoles();
        if ( list != null ) {
            member.roles( new ArrayList<Role>( list ) );
        }
        member.createdDate( memberDTO.getCreatedDate() );

        return member.build();
    }

    @Override
    public Member memberRequestDTOToMember(MemberRequestDTO memberRequestDTO) {
        if ( memberRequestDTO == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.userId( memberRequestDTO.getUserId() );
        member.password( memberRequestDTO.getPassword() );
        member.username( memberRequestDTO.getUsername() );
        member.nickname( memberRequestDTO.getNickname() );
        member.gender( memberRequestDTO.getGender() );
        member.birth( memberRequestDTO.getBirth() );
        member.email( memberRequestDTO.getEmail() );
        member.phoneNo( memberRequestDTO.getPhoneNo() );
        member.zipcode( memberRequestDTO.getZipcode() );
        member.street( memberRequestDTO.getStreet() );
        member.addressDetail( memberRequestDTO.getAddressDetail() );

        return member.build();
    }

    @Override
    public MemberDTO requestDTOToMemberDTO(MemberRequestDTO memberRequestDTO) {
        if ( memberRequestDTO == null ) {
            return null;
        }

        MemberDTO.MemberDTOBuilder memberDTO = MemberDTO.builder();

        memberDTO.userId( memberRequestDTO.getUserId() );
        memberDTO.username( memberRequestDTO.getUsername() );
        memberDTO.nickname( memberRequestDTO.getNickname() );
        memberDTO.gender( memberRequestDTO.getGender() );
        memberDTO.birth( memberRequestDTO.getBirth() );
        memberDTO.email( memberRequestDTO.getEmail() );
        memberDTO.phoneNo( memberRequestDTO.getPhoneNo() );
        memberDTO.zipcode( memberRequestDTO.getZipcode() );
        memberDTO.street( memberRequestDTO.getStreet() );
        memberDTO.addressDetail( memberRequestDTO.getAddressDetail() );
        memberDTO.memberImage( multipartFileToMemberImage( memberRequestDTO.getMemberImage() ) );

        return memberDTO.build();
    }

    protected MemberImage multipartFileToMemberImage(MultipartFile multipartFile) {
        if ( multipartFile == null ) {
            return null;
        }

        MemberImage.MemberImageBuilder<?, ?> memberImage = MemberImage.builder();

        return memberImage.build();
    }
}
