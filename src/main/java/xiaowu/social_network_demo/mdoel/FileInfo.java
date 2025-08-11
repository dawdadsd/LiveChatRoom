package xiaowu.social_network_demo.mdoel;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Data
@Builder
public class FileInfo {
    //文件id
    private String fileId;
    //原始文件名
    private  String originalFilename;
    //上传时间
    private LocalDateTime uploadTime;
    //用户id
    private String userId;
    //群组id
    private  String groupId;
    //文件类型
    private  String fileType;
    //文件大小
    private long fileSize;
    //文件存储路径
    private String storagePath;


    /**
     * 生成安全的存储文件名
     * 格式：HH:mm:ss_原文件名
     * @return 处理后的安全文件名
     */
    public String getSafeFilename() {
        // 获取当前时间并格式化为HH:mm:ss
        String timePrefix = uploadTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        // 拼接时间前缀和原始文件名，并进行安全处理 sanitizeFilename
        return sanitizeFilename(timePrefix + "_" + originalFilename);


    }


     /**
     * 文件名安全处理
     * 1. 将非法字符替换为下划线
     * 2. 防止路径遍历攻击（如../）
     * @param filename 原始文件名
     * @return 处理后的安全文件名
     */
    private String sanitizeFilename(String  filename){
        // 替换所有非字母数字、-、_、.的字符
         filename =  filename.replaceAll("[^a-zA-Z0-9-_.]","_");
        // 防止路径遍历
         filename =   filename.replaceAll("\\.\\.","_");
        return  filename;
}

     public void generateFileId(){
        this.fileId = UUID.randomUUID().toString();
    }



}
