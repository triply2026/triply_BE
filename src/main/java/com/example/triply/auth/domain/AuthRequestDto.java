package com.example.triply.auth.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class AuthRequestDto {

    @Getter
    public static class SignUp {

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, max = 50)
        private String password;

        @NotBlank
        @Size(min = 2, max = 20)
        private String nickname;
    }

    @Getter
    public static class Login {

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }
}
