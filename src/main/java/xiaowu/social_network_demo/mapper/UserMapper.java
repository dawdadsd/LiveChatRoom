package xiaowu.social_network_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xiaowu.social_network_demo.mdoel.StudentUser;
import xiaowu.social_network_demo.mdoel.TeacherUser;
import xiaowu.social_network_demo.mdoel.User;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE user_type = 'STUDENT'")
    List<StudentUser> selectAllStudents();
    @Select("SELECT * FROM users WHERE user_type = 'STUDENT' AND major = #{major}")
    List<StudentUser> selectStudentsByMajor(String major);
    @Select("SELECT * FROM users WHERE user_type = 'TEACHER' AND department = #{department}")
    List<TeacherUser> selectTeachersByDepartment(String department);
}
