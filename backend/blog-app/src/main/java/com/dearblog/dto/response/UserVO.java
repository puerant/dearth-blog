package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String bio;

    private String email;

    private LocalDateTime createdAt;
}
