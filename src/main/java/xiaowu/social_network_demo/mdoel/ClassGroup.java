package xiaowu.social_network_demo.mdoel;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.ibatis.annotations.One;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "class_group")
@Data
public class ClassGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String className;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_group_members",
            joinColumns = @JoinColumn(name = "class_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();
    @OneToMany
    @JoinColumn(name = "class_group_id")
    private Set<Group> groups = new HashSet<>();
}
