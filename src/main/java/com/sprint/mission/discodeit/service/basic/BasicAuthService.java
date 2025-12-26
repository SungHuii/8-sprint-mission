package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
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
     * 닉네임과 비밀번호 기반 기본 인증
     */

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        validateLoginRequest(request);

        String nickname = request.nickname().trim();

        User user = userRepository.findByNickname(nickname)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("닉네임과 비밀번호가 일치하지 않습니다."));

        return toAuthResponse(user);
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

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
