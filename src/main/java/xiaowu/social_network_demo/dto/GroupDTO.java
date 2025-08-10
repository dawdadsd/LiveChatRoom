package xiaowu.social_network_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 群组信息传输对象 - 使用Java 17特性
 *
 * 用于在Controller层与前端交互时传递群组基本信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    /** 群组ID */
    private Integer id;
    /** 群组名称 */
    private String groupName;
    /** 关联的班级ID */
    private Integer classGroupId;
    /** 成员数量 */
    private Integer memberCount;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}
