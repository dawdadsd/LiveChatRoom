package xiaowu.social_network_demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xiaowu.social_network_demo.enums.UserRole;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private Integer id;
    private String username;
    private String password;
    private UserRole role;

}
