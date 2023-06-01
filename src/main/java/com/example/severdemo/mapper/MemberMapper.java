package com.example.severdemo.mapper;

import com.example.severdemo.domain.user.Member;
import com.example.severdemo.domain.user.MemberDTO;
import com.example.severdemo.domain.user.MemberRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberDTO memberToMemberDTO(Member member);

    @Mapping(target = "password", ignore = true)
    Member memberDTOToMember(MemberDTO memberDTO);

    @Mapping(target = "social", ignore = true)
    @Mapping(target = "provider", ignore = true)
    Member memberRequestDTOToMember(MemberRequestDTO memberRequestDTO);

    @Mapping(target = "social", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    MemberDTO requestDTOToMemberDTO(MemberRequestDTO memberRequestDTO);
}
