package xiaowu.social_network_demo.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import xiaowu.social_network_demo.mdoel.FileInfo;

import java.io.IOException;

public interface FileStorageService {


    /**
     * 上传文件到服务器
     * @param file 上传的文件
     * @param groupId 群组ID
     * @param userId 用户ID
     * @return 文件信息对象
     * @throws java.io.IOException 文件IO异常
     * @throws FileUploadException 文件上传业务异常
     */
    FileInfo uploadfile(MultipartFile file, String userId, String groupId)
            throws IOException,FileUploadException;
}
