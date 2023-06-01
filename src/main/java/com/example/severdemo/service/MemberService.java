package com.example.severdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.severdemo.ImageDTO;
import com.example.severdemo.domain.token.TokenDTO;
import com.example.severdemo.domain.user.*;
import com.example.severdemo.mapper.MemberMapper;
import com.example.severdemo.repository.MemberImageRepository;
import com.example.severdemo.repository.MemberRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Log4j2
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberImageRepository imageRepository;
    private final TokenService tokenService;
    private final FileService fileService;
    private final AuthService authService;

    @Value("${spring.multipart.location}")
    private String uploadPath;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return memberRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("userId: " + userId + "를 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRoles().stream().map(Role::getType).collect(Collectors.joining(",")));

        return new User(
                member.getUserId(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    //이미지를 eager로 불러옴
    public Member findMemberByUserId(String userId) {
        return memberRepository.findByUserIdEagerLoadImage(userId)
                .orElseThrow(() -> new RuntimeException("해당 ID를 가진 사용자가 존재하지 않습니다."));
    }

    public MemberDTO findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("해당 email을 가진 사용자가 존재하지 않습니다."));
        return MemberMapper.INSTANCE.memberToMemberDTO(member);
    }

    public MemberDTO getMember(String userId) {
        return MemberMapper.INSTANCE.memberToMemberDTO(findMemberByUserId(userId));
    }

    @Transactional
    public void saveMember(MemberDTO memberDTO) {
        memberRepository.save(MemberMapper.INSTANCE.memberDTOToMember(memberDTO));
    }

    /**
     * UsernamePasswordAuthenticationToken을 통한 Spring Security인증 진행
     * 이후 tokenService에 userId값을 전달하여 토큰 생성
     * @param requestDTO
     * @return TokenDTO
     */
    @Transactional
    public TokenDTO login(LoginRequestDTO requestDTO) {
        authService.authenticateLogin(requestDTO);

        Member member = memberRepository.findByUserId(requestDTO.getUserId()).get();
        return tokenService.createToken(member);
    }

    @Transactional(readOnly = false)
    public void signup(MemberRequestDTO requestDTO) {
        if(memberRepository.existsByUserId(requestDTO.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Member member = MemberMapper.INSTANCE.memberRequestDTOToMember(requestDTO);
        member.updateRole(Role.ROLE_USER);

        if(!(requestDTO.getMemberImage() == null)) {
            MemberImage memberImage = saveMemberImage(requestDTO.getMemberImage());
            member.updateMemberImage(memberImage);
        }

        memberRepository.save(member);

    }

    @Transactional(readOnly = false)
    public void updateMember(MemberRequestDTO memberRequestDTO, String userId) {

        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        //중복가입  에러해결필요
        if(member.isSocial()) {
            if(!memberRequestDTO.getEmail().equals(member.getEmail())) {
                throw new RuntimeException("소셜회원은 이메일 변경이 불가합니다.");
            }
        }

        if((member.getMemberImage() != null) && (memberRequestDTO.getMemberImage() != null)) {
            imageRepository.deleteById(member.getMemberImage().getId());
        }
        MemberDTO memberDTO = MemberMapper.INSTANCE.requestDTOToMemberDTO(memberRequestDTO);
        memberDTO.setRoles(member.getRoles());
        memberDTO.setCreatedDate(member.getCreatedDate());
        memberDTO.setSocial(member.isSocial());
        memberDTO.setProvider(member.getProvider());

        if(!(memberRequestDTO.getMemberImage() == null)) {
            MemberImage memberImage = saveMemberImage(memberRequestDTO.getMemberImage());
            memberDTO.setMemberImage(memberImage);
        }

        Member updatedMember = MemberMapper.INSTANCE.memberDTOToMember(memberDTO);
        if(memberRequestDTO.getPassword() == null) {
            updatedMember.updatePassword(memberRequestDTO.getPassword());
        }
        else {
            updatedMember.updatePassword(member.getPassword());
        }

        memberRepository.save(updatedMember);
    }

    @Transactional(readOnly = false)
    public MemberImage saveMemberImage(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        Path root = Paths.get(uploadPath, "member");

        try {
            ImageDTO imageDTO =  fileService.createImageDTO(originalName, root);
            MemberImage memberImage = MemberImage.builder()
                    .uuid(imageDTO.getUuid())
                    .fileName(imageDTO.getFileName())
                    .fileUrl(imageDTO.getFileUrl())
                    .build();

            file.transferTo(Paths.get(imageDTO.getFileUrl()));

            return imageRepository.save(memberImage);
        } catch (IOException e) {
            log.warn("업로드 폴더 생성 실패: " + e.getMessage());
        }

        return null;
    }
}
