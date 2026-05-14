package com.example.triply.auth.controller;

import com.example.triply.auth.domain.AuthRequestDto;
import com.example.triply.auth.domain.AuthResponseDto;
import com.example.triply.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto.MemberInfo> signUp(
            @Valid @RequestBody AuthRequestDto.SignUp request) {
        AuthResponseDto.MemberInfo response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto.MemberInfo> login(
            @Valid @RequestBody AuthRequestDto.Login request,
            HttpSession session) {
        AuthResponseDto.MemberInfo response = authService.login(request);
        session.setAttribute("memberId", response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
