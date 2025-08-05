package xiaowu.social_network_demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一响应结果封装类 - 使用Java 17特性
 * 
 * @author xiaowu
 * @since Java 17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    
    private boolean success;
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // 私有构造器
    private Result(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应 - 带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, 200, "操作成功", data);
    }

    /**
     * 成功响应 - 带数据和消息
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, 200, message, data);
    }

    /**
     * 成功响应 - 仅消息
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(true, 200, message, null);
    }

    /**
     * 失败响应 - 带消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(false, 500, message, null);
    }

    /**
     * 失败响应 - 带状态码和消息
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(false, code, message, null);
    }

    /**
     * 失败响应 - 带状态码、消息和数据
     */
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    /**
     * 认证失败响应
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(false, 401, message, null);
    }

    /**
     * 权限不足响应
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(false, 403, message, null);
    }

    /**
     * 资源不存在响应
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(false, 404, message, null);
    }

    /**
     * 参数错误响应
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(false, 400, message, null);
    }
}
