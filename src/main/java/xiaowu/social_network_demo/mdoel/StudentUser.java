package xiaowu.social_network_demo.mdoel;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xiaowu.social_network_demo.enums.UserRole;
import xiaowu.social_network_demo.mdoel.User;

@Entity
@DiscriminatorValue("STUDENT")
@TableName("users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StudentUser extends User {

    @Column(name = "student_id")
    private String studentId; // 学号

    @Column(name = "major")
    private String major; // 专业

    @Column(name = "grade")
    private Integer grade; // 年级

    @Column(name = "class_name")
    private String className; // 班级

    @Column(name = "college")
    private String college; // 学院

    @Column(name = "enrollment_year")
    private Integer enrollmentYear; // 入学年份

    public StudentUser(String username, String password, String realName, String email) {
        super();
        this.setUsername(username);
        this.setPassword(password);
        this.setRealName(realName);
        this.setEmail(email);
        this.setRole(UserRole.STUDENT);
    }

    @Override
    public String getUserType() {
        return "STUDENT";
    }

    // 学生特有的业务方法
    public String getFullStudentInfo() {
        return String.format("%s - %s级 %s专业 %s",
                getRealName(), grade, major, className);
    }

    public boolean isGraduated() {
        return enrollmentYear != null &&
                (java.time.Year.now().getValue() - enrollmentYear) >= 4;
    }
}