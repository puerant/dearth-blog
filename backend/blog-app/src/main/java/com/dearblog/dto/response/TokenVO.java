package com.dearblog.dto.response;

import lombok.Data;

@Data
public class TokenVO {

    private String token;

    /** Token 有效期（秒） */
    private Long expiresIn;

    public static TokenVO of(String token, long expiresIn) {
        TokenVO vo = new TokenVO();
        vo.token = token;
        vo.expiresIn = expiresIn;
        return vo;
    }
}
