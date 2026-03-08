package com.dearblog.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String nickname;
    private String avatar;
    private String bio;
    private String email;
}
