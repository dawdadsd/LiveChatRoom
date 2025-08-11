package xiaowu.social_network_demo.controller;


import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xiaowu.social_network_demo.common.Result;
import xiaowu.social_network_demo.mdoel.FileInfo;
import xiaowu.social_network_demo.service.FileStorageService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    /**
     * 文件上传接口
     * @param file 上传的文件（MultipartFile类型）
     * @param groupId 群组ID
     * @param userId 用户ID
     * @return 统一响应格式
     *
     * 处理流程：
     * 1. 调用服务层上传文件
     * 2. 成功返回文件信息
     * 3. 失败返回对应错误信息
     */
    @PostMapping("/upload")
    public Result<FileInfo> upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("groupId") String groupId) {
        try {
            FileInfo fileInfo = fileStorageService.uploadfile(file, userId, groupId);
            return Result.success(fileInfo);
        } catch (FileUploadException e) {
            return Result.error(e.getMessage());
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

    }




    }


