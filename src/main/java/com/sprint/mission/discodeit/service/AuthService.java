package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthResponse;
import com.sprint.mission.discodeit.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
