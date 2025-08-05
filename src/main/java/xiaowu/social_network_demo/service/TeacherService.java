package xiaowu.social_network_demo.service;

import xiaowu.social_network_demo.dto.UserInfoResponse;

import java.util.List;
import java.util.Map;

/**
 * 教师服务接口 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
public interface TeacherService {
    
    /**
     * 获取所有学生列表
     */
    List<UserInfoResponse> getAllStudents();
    
    /**
     * 根据专业获取学生列表
     */
    List<UserInfoResponse> getStudentsByMajor(String major);
    
    /**
     * 获取同院系教师列表
     */
    List<UserInfoResponse> getColleagues(String teacherUsername);
    
    /**
     * 更新教师专业信息
     */
    boolean updateProfessionalInfo(String username, UserInfoResponse updateRequest);
    
    /**
     * 获取教师统计信息
     */
    Map<String, Object> getTeacherStatistics(String username);
}
