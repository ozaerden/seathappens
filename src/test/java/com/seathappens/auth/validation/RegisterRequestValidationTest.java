package com.seathappens.auth.validation;

import com.seathappens.auth.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        RegisterRequest request = new RegisterRequest(
                "ozan@example.com",
                "Password123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenPasswordContainsWhitespace() {
        RegisterRequest request = new RegisterRequest(
                "ozan@example.com",
                "Password 123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenPasswordContainsTurkishCharacter() {
        RegisterRequest request = new RegisterRequest(
                "ozan@example.com",
                "Şifre123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenPasswordTooShort() {
        RegisterRequest request = new RegisterRequest(
                "ozan@example.com",
                "Ab1!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        RegisterRequest request = new RegisterRequest(
                "invalid-email",
                "Password123!"
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

}
