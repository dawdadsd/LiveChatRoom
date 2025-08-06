package xiaowu.social_network_demo.exception;

public class GroupException extends RuntimeException {
    public GroupException(String message) {
        super(message);
    }

    public GroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class GroupNotFoundException extends GroupException {
        public GroupNotFoundException(Long groupId) {
            super("群组不存在，ID: " + groupId);
        }
    }

    public static class UserAlreadyInGroupException extends GroupException {
        public UserAlreadyInGroupException(Integer userId, Long groupId) {
            super("用户 " + userId + " 已经在群组 " + groupId + " 中");
        }
    }

    public static class UserNotInGroupException extends GroupException {
        public UserNotInGroupException(Integer userId, Long groupId) {
            super("用户 " + userId + " 不在群组 " + groupId + " 中");
        }
    }

    public static class GroupNameAlreadyExistsException extends GroupException {
        public GroupNameAlreadyExistsException(String groupName) {
            super("群组名称已存在: " + groupName);
        }
    }
}