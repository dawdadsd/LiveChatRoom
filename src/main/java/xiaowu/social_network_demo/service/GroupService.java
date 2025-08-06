package xiaowu.social_network_demo.service;

import xiaowu.social_network_demo.mdoel.Group;
import xiaowu.social_network_demo.mdoel.User;

import java.util.Set;

public interface GroupService {
    /**
     * 创建分组
     * @param groupName 群组名称
     * @param classGroupId 群组Id
     * @return 返回一个群组消息结构
     */
    Group createGroup(String groupName,Integer classGroupId);

    /**
     * 邀请用户进入分组
     */
    void inviteUserToGroup(Integer groupId,Integer userId);

    /**
     *获取群组下的所有用户
     */
    Set<User> getGroupMembers(Integer groupId);

    /**
     *删除群组<权限，只有教师可以访问和删除></>
     */
    void deleteGroup(Integer groupId);

    /**
     *根据分组Id查找分组
     */
    Group getGroupId(Integer groupId);
}
