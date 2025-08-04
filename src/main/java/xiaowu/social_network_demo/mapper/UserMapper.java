package xiaowu.social_network_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xiaowu.social_network_demo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
