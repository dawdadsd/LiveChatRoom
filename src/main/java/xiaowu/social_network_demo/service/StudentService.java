package xiaowu.social_network_demo.service;

import xiaowu.social_network_demo.dto.UserInfoResponse;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
public interface StudentService {
    
    /**
     * 获取同专业同学列表
     */
    List<UserInfoResponse> getClassmates(String studentUsername);
    
    /**
     * 获取本专业教师列表
     */
    List<UserInfoResponse> getMajorTeachers(String studentUsername);
    
    /**
     * 更新学生学业信息
     */
    boolean updateAcademicInfo(String username, UserInfoResponse updateRequest);
    
    /**
     * 获取学生统计信息
     */
    Map<String, Object> getStudentStatistics(String username);
    
    /**
     * 检查是否已毕业
     */
    boolean checkGraduationStatus(String username);
}
