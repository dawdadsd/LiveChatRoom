package xiaowu.social_network_demo.mdoel;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discussion_group")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 组ID，唯一标识

    @Column(nullable = false, unique = true)
    private String groupName; // 组名

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>(); // 组成员，支持多对多关系

    @Enumerated(EnumType.STRING)
    private GroupType type; // 组的类型，比如公开讨论组、私密小组等

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 组创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 组更新时间

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }
}

