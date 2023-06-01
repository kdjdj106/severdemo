package com.example.severdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.severdemo.domain.auth.OAuth2Attribute;
import com.example.severdemo.domain.auth.Provider;
import com.example.severdemo.domain.user.Member;
import com.example.severdemo.domain.user.Role;
import com.example.severdemo.mapper.MemberMapper;
import com.example.severdemo.repository.MemberRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    public Map<String, Object> findOrSaveMember(String id_token, String provider) throws ParseException, JsonProcessingException {
        OAuth2Attribute oAuth2Attribute;
        switch (provider) {
            case "google":
                oAuth2Attribute = getGoogleData(id_token);
                break;
            case "kakao":
                oAuth2Attribute = createKakaoUser(id_token);
                break;
            default:
                throw new RuntimeException("제공하지 않는 인증기관입니다.");
        }

        Integer httpStatus = HttpStatus.CREATED.value();

        log.info("이제부터 멤버 저장");
        log.info("userId :"+oAuth2Attribute.getUserId());
        log.info("email :"+oAuth2Attribute.getEmail());
        log.info("provider :"+Provider.of(provider));
        log.info("username :"+oAuth2Attribute.getUsername());
        Member member = memberRepository.findByEmail(oAuth2Attribute.getEmail())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .userId(oAuth2Attribute.getUserId())
                            .email(oAuth2Attribute.getEmail())
                            .social(true)
                            .provider(Provider.of(provider))
                            .username(oAuth2Attribute.getUsername())
                            .build();

                    newMember.updateRole(Role.ROLE_USER);
                    return memberRepository.save(newMember);
                });
        log.info("이제부터 멤버 저장 끝");
        if(!member.isSocial()) {
            httpStatus = HttpStatus.OK.value();
            member.updateSocial(Provider.of(provider));
            memberRepository.save(member);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dto", MemberMapper.INSTANCE.memberToMemberDTO(member));
        result.put("status", httpStatus);

        return result;
    }

    private OAuth2Attribute getGoogleData(String id_token)  throws ParseException, JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String googleApi = "https://oauth2.googleapis.com/tokeninfo";
        String targetUrl = UriComponentsBuilder.fromHttpUrl(googleApi).queryParam("id_token", id_token).build().toUriString();

        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(response.getBody());
        log.info(jsonBody.toString());
        Map<String, Object> body = new ObjectMapper().readValue(jsonBody.toString(), Map.class);

        return OAuth2Attribute.of("google", "sub", body);
    }

    public OAuth2Attribute createKakaoUser(String token) throws RuntimeException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            JSONParser jsonParser = new JSONParser();




            Long id = element.getAsJsonObject().get("id").getAsLong();
            String profileimage = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image").getAsString();
            String nickname = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("profileimage"+ profileimage);


            result = result.replaceAll("\\\"", "");
            log.info("여기서 부터 가공"+element.toString());
            log.info("result"+result);

            Map<String, Object> body = new HashMap<>();

            body.put("id", id);
            body.put("email", email);
            body.put("profileimage", profileimage);
            body.put("nickname", nickname);

            Map<String, String> body2 = new HashMap<>();
            body2.put("id", id.toString()+"kakao");
            body2.put("email", email);
            body2.put("profileimage", profileimage);
            body2.put("nickname", nickname);

            log.info("Map 가공후 :" + body2.get(email));

            br.close();

            return OAuth2Attribute.of("kakao", "kakao", body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
