package com.seathappens.ticket.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class TicketCodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generate() {
        byte[] randomBytes = new byte[16];
        SECURE_RANDOM.nextBytes(randomBytes);

        return "TKT-" + HexFormat.of().formatHex(randomBytes).toUpperCase();
    }

}
