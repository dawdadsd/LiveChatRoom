package xiaowu.social_network_demo.mdoel;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xiaowu.social_network_demo.enums.UserRole;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TEACHER")
@TableName("users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TeacherUser extends User {

    @Column(name = "employee_id")
    private String employeeId; // 工号

    @Column(name = "department")
    private String department; // 院系

    @Column(name = "title")
    private String title; // 职称（教授、副教授、讲师等）

    @Column(name = "office_location")
    private String officeLocation; // 办公室位置

    @Column(name = "research_area")
    private String researchArea; // 研究方向

    @Column(name = "hire_date")
    private java.time.LocalDate hireDate; // 入职日期

    public TeacherUser(String username, String password, String realName, String email) {
        super();
        this.setUsername(username);
        this.setPassword(password);
        this.setRealName(realName);
        this.setEmail(email);
        this.setRole(UserRole.TEACHER);
    }

    @Override
    public String getUserType() {
        return "TEACHER";
    }

    // 教师特有的业务方法
    public String getFullTeacherInfo() {
        return String.format("%s %s - %s",
                title != null ? title : "", getRealName(), department);
    }

    public boolean isSeniorTeacher() {
        return "教授".equals(title) || "副教授".equals(title);
    }

    public int getWorkingYears() {
        if (hireDate == null) return 0;
        return java.time.Period.between(hireDate, java.time.LocalDate.now()).getYears();
    }
}