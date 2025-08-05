package xiaowu.social_network_demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.mapper.UserMapper;

import java.util.List;

/**
 * 自定义用户详情服务
 *
 * @author xiaowu
 * @since Java 17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("正在加载用户信息: {}", username);

        // 1. 根据用户名查询数据库
        var user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", username)
        );

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 检查用户状态
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("账户已被禁用");
        }

        log.debug("成功加载用户: {}, 角色: {}", username, user.getRole());

        // 2. 转换为 Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
