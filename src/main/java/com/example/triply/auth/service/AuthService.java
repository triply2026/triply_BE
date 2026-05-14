package com.example.triply.auth.service;

import com.example.triply.auth.domain.AuthRequestDto;
import com.example.triply.auth.domain.AuthResponseDto;
import com.example.triply.auth.repository.AuthRepository;
import com.example.triply.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 신규 회원을 등록한다.
     * 이메일 중복 시 IllegalArgumentException을 던진다.
     *
     * @param request 회원가입 요청 DTO (email, password, nickname)
     * @return 생성된 회원의 요약 정보
     */
    @Transactional
    public AuthResponseDto.MemberInfo signUp(AuthRequestDto.SignUp request) {
        if (authRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        Member saved = authRepository.save(member);

        return AuthResponseDto.MemberInfo.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .nickname(saved.getNickname())
                .build();
    }

    /**
     * 이메일과 비밀번호를 검증하여 회원 정보를 반환한다.
     * 이메일이 존재하지 않거나 비밀번호가 일치하지 않으면 IllegalArgumentException을 던진다.
     *
     * @param request 로그인 요청 DTO (email, password)
     * @return 인증된 회원의 요약 정보
     */
    @Transactional(readOnly = true)
    public AuthResponseDto.MemberInfo login(AuthRequestDto.Login request) {
        Member member = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return AuthResponseDto.MemberInfo.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
