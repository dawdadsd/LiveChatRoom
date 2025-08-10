package xiaowu.social_network_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import xiaowu.social_network_demo.mdoel.Group;
import xiaowu.social_network_demo.mdoel.User;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    @Insert("INSERT INTO group_members (group_id, user_id) VALUES (#{groupId}, #{userId})")
    void addUserToGroup(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

    @Delete("DELETE FROM group_members WHERE group_id = #{groupId} AND user_id = #{userId}")
    void removeUserFromGroup(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

    @Select("SELECT u.* FROM users u JOIN group_members gm ON u.id = gm.user_id WHERE gm.group_id = #{groupId}")
    List<User> selectGroupMembers(@Param("groupId") Integer groupId);

    @Select("SELECT COUNT(*) FROM group_members WHERE group_id = #{groupId}")
    int countGroupMembers(@Param("groupId") Integer groupId);

    @Select("SELECT COUNT(*) > 0 FROM group_members WHERE group_id = #{groupId} AND user_id = #{userId}")
    boolean isUserInGroup(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

    boolean isMemberInGroup(@Param("groupId") Integer groupId,@Param("userIp") String userIp);

    List<String> getGroupMemberIps(@Param("groupId") Integer groupId);
}
