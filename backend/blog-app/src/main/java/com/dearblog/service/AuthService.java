package com.dearblog.service;

import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.LoginRequest;
import com.dearblog.dto.response.TokenVO;
import com.dearblog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${blog.jwt.expiration}")
    private long jwtExpiration;

    public TokenVO login(LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            String token = jwtUtil.generateToken(auth.getName());
            return TokenVO.of(token, jwtExpiration);
        } catch (BadCredentialsException e) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
    }

    public void logout(String token) {
        jwtUtil.invalidateToken(token);
    }
}
