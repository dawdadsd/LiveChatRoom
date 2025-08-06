package xiaowu.social_network_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaowu.social_network_demo.mapper.GroupMapper;
import xiaowu.social_network_demo.mapper.UserMapper;
import xiaowu.social_network_demo.mdoel.Group;
import xiaowu.social_network_demo.mdoel.User;
import xiaowu.social_network_demo.service.GroupService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {
    private final  GroupMapper groupMapper;
    private final UserMapper userMapper;

    @Override
    public Group createGroup(String groupName, Integer classGroupId) {
        if(StringUtils.isBlank(groupName))
        {
            throw new IllegalArgumentException("群组名称不能为空");
        }
        QueryWrapper<Group> wrapper = new QueryWrapper<>();
        wrapper.eq("group_name",groupName);
        if(groupMapper.selectOne(wrapper) != null)
        {
            log.warn("群组已存在:{}" ,groupName);
            throw new IllegalArgumentException("群组名称已存在 :" + groupName);
        }
        Group group = new Group();
        group.setGroupName(groupName);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        groupMapper.insert(group);
        log.info("成功创建群组");
        return group;
    }

    @Override
    public void inviteUserToGroup(Integer groupId, Integer userId) {
        // 1. 验证群组是否存在
        // 2. 验证用户是否存在
        // 3. 检查用户是否已在群组中
        // 4. 执行邀请操作
        // 5. 更新群组成员关系
        // 6. 记录操作日志
        Group group = getGroupId(groupId);
        if(group == null)
        {
            throw new IllegalArgumentException("groupId 为空");
        }
        User user = userMapper.selectById(userId);
        if(user ==null)
        {
            log.warn("用户Id不存在 : {}",userId);
            throw new IllegalArgumentException("用户Id不存在: " + userId);
        }
        if(groupMapper.isUserInGroup(groupId,userId)){
            log.warn("用户已在群组中: userId={}, groupId={}", userId, groupId);
            throw new IllegalArgumentException("用户已在群组中");
        }
        groupMapper.addUserToGroup(groupId,userId);
        log.info("成功邀请用户进群: userId={}, groupId={}", userId, groupId);
    }

    @Override
    public Set<User> getGroupMembers(Integer groupId) {
        Group group = getGroupId(groupId);
        if(group == null)
        {
            throw new IllegalArgumentException("groupId 为空");
        }
        List<User> userList = groupMapper.selectGroupMembers(groupId);
        return new HashSet<>(userList);
    }

    @Override
    public void deleteGroup(Integer groupId) {

    }

    @Override
    public Group getGroupId(Integer groupId) {
        //TODO : 此处可以加缓存
        if(groupId == null)
        {
            log.warn("查询群组失败,groupId不能为空");
            throw new IllegalArgumentException("群组ID不能为空");
        }
        log.debug("查询群组 : groupId = {}",groupId);
        Group group = groupMapper.selectById(groupId);
        if(group == null)
        {
            log.warn("群组不存在: groupId = {}" , groupId);
            throw new IllegalArgumentException("群组Id不存在，ID" + groupId);
        }
        log.debug("成功查询到群组 ： groupId = {} ,groupName = {}" ,groupId, group.getGroupName());
        return group;
    }
}
