package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.PasswordUpdateRequest;
import com.dearblog.dto.request.UserUpdateRequest;
import com.dearblog.dto.response.UserVO;
import com.dearblog.entity.BlogUser;
import com.dearblog.mapper.BlogUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BlogUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserVO getMe() {
        return toVO(getCurrentUser());
    }

    public UserVO updateMe(UserUpdateRequest req) {
        BlogUser user = getCurrentUser();
        if (req.getNickname() != null) user.setNickname(req.getNickname());
        if (req.getAvatar() != null) user.setAvatar(req.getAvatar());
        if (req.getBio() != null) user.setBio(req.getBio());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        userMapper.updateById(user);
        return toVO(user);
    }

    public void updatePassword(PasswordUpdateRequest req) {
        BlogUser user = getCurrentUser();
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_WRONG);
        }
        userMapper.update(null, new LambdaUpdateWrapper<BlogUser>()
                .set(BlogUser::getPassword, passwordEncoder.encode(req.getNewPassword()))
                .eq(BlogUser::getId, user.getId()));
    }

    private BlogUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BlogUser user = userMapper.selectOne(new LambdaQueryWrapper<BlogUser>()
                .eq(BlogUser::getUsername, username));
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return user;
    }

    private UserVO toVO(BlogUser user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
