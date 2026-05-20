package com.seathappens.auth.dto.response;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email
) {
}
