package com.dearblog.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dearblog.entity.BlogUser;
import com.dearblog.mapper.BlogUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security 用户详情服务
 * 从数据库加载用户信息用于认证
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BlogUserMapper blogUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BlogUser user = blogUserMapper.selectOne(
                new LambdaQueryWrapper<BlogUser>()
                        .eq(BlogUser::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return new User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
}
