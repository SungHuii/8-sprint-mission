package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthResponse;
import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    /*
    * AuthService 구현체
    * 기본 인증 서비스 (닉네임, 비밀번호 기반)
    * */

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        // 로그인 요청 검증 메서드
        validateLoginRequest(request);

        // 닉네임으로 찾고, 비밀번호 일치 여부 확인
        User user = userRepository.findByNickname(request.nickname())
                .filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("username 또는 password가 일치하지 않습니다."));

        return toAuthResponse(user);
    }

    // 로그인 요청 검증 메서드
    private void validateLoginRequest(LoginRequest request) {
        if (request == null) throw new IllegalArgumentException("요청이 null입니다.");
        if (request.nickname() == null || request.nickname().isBlank())
            throw new IllegalArgumentException("username은 필수입니다.");
        if (request.password() == null || request.password().isBlank())
            throw new IllegalArgumentException("password는 필수입니다.");
    }

    // User Entity를 AuthResponse DTO로 변환하는 메서드
    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfileId()
        );
    }
}
