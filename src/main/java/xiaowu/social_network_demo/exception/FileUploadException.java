package xiaowu.social_network_demo.exception;


/**
 * 文件上传业务异常
 * 作用：统一处理文件上传过程中的业务错误
 * <p>
 * 使用场景：
 * - 文件类型不支持
 * - 文件大小超标
 * - 存储空间不足等
 */
public class FileUploadException extends Exception {

    public    FileUploadException (String message){
        super(message);
    }
    public    FileUploadException (String message,Throwable cause){
        super(message,cause);
    }


}
