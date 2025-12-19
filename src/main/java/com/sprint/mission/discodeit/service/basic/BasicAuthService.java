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
    * AuthService 援ы쁽泥?
    * 湲곕낯 ?몄쬆 ?쒕퉬??(?됰꽕?? 鍮꾨?踰덊샇 湲곕컲)
    * */

    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        // 濡쒓렇???붿껌 寃利?硫붿꽌??
        validateLoginRequest(request);

        // ?됰꽕?꾩쑝濡?李얘퀬, 鍮꾨?踰덊샇 ?쇱튂 ?щ? ?뺤씤
        User user = userRepository.findByNickname(request.nickname())
                .filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("username ?먮뒗 password媛 ?쇱튂?섏? ?딆뒿?덈떎."));

        return toAuthResponse(user);
    }

    // 濡쒓렇???붿껌 寃利?硫붿꽌??
    private void validateLoginRequest(LoginRequest request) {
        if (request == null) throw new IllegalArgumentException("?붿껌??null?낅땲??");
        if (request.nickname() == null || request.nickname().isBlank())
            throw new IllegalArgumentException("username? ?꾩닔?낅땲??");
        if (request.password() == null || request.password().isBlank())
            throw new IllegalArgumentException("password???꾩닔?낅땲??");
    }

    // User Entity瑜?AuthResponse DTO濡?蹂?섑븯??硫붿꽌??
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

