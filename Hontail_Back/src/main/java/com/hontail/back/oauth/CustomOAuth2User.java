// OAuth2 로그인한 사용자의 정보를 가져오고, DB에서 확인하거나 새로 저장하는 역할
package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    // Spring Security가 로그인한 사용자를 OAuth2User 타입으로 다루기 때문에 직접 구현 필요 >> implements 선언
    // 이를 통해 로그인한 사용자 정보를 CustomOAuth2User 객체로 관리할 수 있음
    // OAuth2User 인터페이스에는 3개의 필수 메서드가 있음
    // >>OAuth2User 인터페이스를 구현하면 getAttributes(), getAuthorities(), getName()을 필수로 오버라이드해야 함

    //한 번 생성된 CustomOAuth2User 객체에서 user 값을 바꾸지 않도록 보장
    //OAuth2 로그인 시, 사용자 정보를 변경하면 안 되기 때문 (불변성을 유지해야 함) >> final 선언
    private final User user;
    private final Map<String, Object> attributes;


    //  user → DB에서 가져온 사용자 정보 (User 엔티티)
    //  attributes → OAuth2 제공자(Google/Naver)에서 받은 원본 사용자 정보 (Map 형태)
    //  이 두 개를 CustomOAuth2User 객체에 저장해서 Spring Security가 사용할 수 있도록 만듦
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override // 원본 OAuth2 사용자 정보 반환
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    // 사용자 권한 정보 반환
    @Override // 사용자 role(user, admin) 확인 >> 사용x but oauth2user의 필수 메서드라 삭제XXX
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return user.getUserNickname();
    } // 닉네임

    public String getUserEmail() {
        return user.getUserEmail();
    } // 이메일

    public String getUserImageUrl() {
        return user.getUserImageUrl();
    } // 이미지

    public Integer getUserId() {
        return user.getId();
    } // id

    public Map<String, Object> getUserInfo() {
        return Map.of(
                "id", getUserId(),
                "email", getUserEmail(),
                "nickname", getName(),
                "profileImage", getUserImageUrl()
        ); // 받아온 정보 로그인하면 출력해줌
    }
}