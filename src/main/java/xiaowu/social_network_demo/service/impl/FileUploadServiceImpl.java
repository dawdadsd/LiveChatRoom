package xiaowu.social_network_demo.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xiaowu.social_network_demo.common.FileStorageMonitor;
import xiaowu.social_network_demo.exception.FileUploadException;
import xiaowu.social_network_demo.mdoel.FileInfo;
import xiaowu.social_network_demo.service.FileStorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * 文件上传服务实现类
 * 作用：实现文件上传的核心业务逻辑
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class FileUploadServiceImpl implements FileStorageService {

    // 最大并发上传数（防止服务器过载）
    private static final int MAX_CONCURRENT_UPLOADS = 20;

    // 使用信号量控制并发（线程安全）
    private final Semaphore uploadSemaphore = new Semaphore(MAX_CONCURRENT_UPLOADS);

    // 允许的文件扩展名白名单（按类别分组）
    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS = Map.of(
            "image", Set.of("jpg", "jpeg", "png", "gif", "webp"),
            "document", Set.of("pdf", "doc", "docx", "txt", "md"),
            "archive", Set.of("zip", "rar", "7z")
    );

    // 各类文件的最大大小限制（字节）
    private static final Map<String, Long> MAX_SIZES = Map.of(
            "image", 10L * 1024 * 1024,    // 10MB
            "document", 50L * 1024 * 1024, // 50MB
            "archive", 100L * 1024 * 1024  // 100MB
    );

    // 文件上传基础目录（从application.yml读取）
    @Value("${file.upload.base-dir:uploads}")
    private String baseUploadDir;

    // 使用Apache Tika检测文件真实类型
    private final Tika tika = new Tika();

    // 存储监控服务（通过构造器注入）
    private final FileStorageMonitor storageMonitor;



    /**
     * 初始化方法（在Bean创建后执行）
     * 作用：确保上传目录存在
     */
    @PostConstruct
    public void init() throws IOException {
        // 创建基础上传目录（如果不存在）
        Path uploadPath = Paths.get(baseUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    /**
     * 上传文件主方法
     * 实现步骤：
     * 1. 获取并发许可
     * 2. 验证文件
     * 3. 检查存储空间
     * 4. 创建文件信息
     * 5. 构建存储路径
     * 6. 保存文件
     * 7. 返回文件信息
     */



    /**
     * 验证上传文件
     * @param file 上传的文件
     * @throws FileUploadException 业务验证失败
     * @throws IOException 文件读取失败
     */
    private void validateFile(MultipartFile file) throws FileUploadException, IOException {
        // 1. 基本检查
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("请选择要上传的文件");
        }

        // 2. 检查文件名
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new FileUploadException("文件名不能为空");
        }

        // 3. 获取文件扩展名
        String extension = getFileExtension(filename).toLowerCase();
        if (extension.isEmpty()) {
            throw new FileUploadException("文件缺少扩展名");
        }

        // 4. 检查扩展名是否在白名单中
        String fileCategory = getFileCategoryByExtension(extension);
        if (fileCategory == null) {
            throw new FileUploadException("不支持的文件类型: " + extension);
        }

        // 5. 检查文件大小
        long fileSize = file.getSize();
        long maxSize = MAX_SIZES.get(fileCategory);
        if (fileSize > maxSize) {
            throw new FileUploadException(
                    String.format("%s类文件大小不能超过%.1fMB",
                            fileCategory, maxSize / (1024.0 * 1024)));
        }

        // 6. 使用Tika检测文件真实类型（防止伪造扩展名）
        String realMimeType = tika.detect(file.getInputStream());
        if (!isMimeTypeAllowed(realMimeType, fileCategory)) {
            throw new FileUploadException("文件类型不匹配，检测到实际类型为: " + realMimeType);
        }
    }

    /**
     * 创建文件信息对象
     */
    private FileInfo createFileInfo(MultipartFile file, String groupId, String userId) {
        FileInfo fileInfo = FileInfo.builder().build();
        fileInfo.setOriginalFilename(file.getOriginalFilename());
        fileInfo.setGroupId(groupId);
        fileInfo.setUserId(userId);
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setFileSize(file.getSize());
        return fileInfo;
    }

    /**
     * 构建文件存储路径
     * 格式：baseDir/群组ID/年月日/用户ID/文件名
     */
    private Path buildStoragePath(FileInfo fileInfo) {
        // 获取当前日期（格式：yyyyMMdd）
        String dateDir = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // 构建完整路径
        return Paths.get(
                baseUploadDir,
                fileInfo.getGroupId(),
                dateDir,
                fileInfo.getUserId(),
                fileInfo.getSafeFilename()
        );
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 根据扩展名获取文件分类
     */
    private String getFileCategoryByExtension(String extension) {
        for (Map.Entry<String, Set<String>> entry : ALLOWED_EXTENSIONS.entrySet()) {
            if (entry.getValue().contains(extension)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 检查MIME类型是否允许
     */
    private boolean isMimeTypeAllowed(String mimeType, String category) {
        // 简单实现：检查MIME类型是否包含类别关键字
        return mimeType != null && mimeType.startsWith(category + "/");
    }

    /**
     * 确定文件类型
     */
    private String determineFileType(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        return getFileCategoryByExtension(extension);
    }

    @Override
    public FileInfo uploadfile(MultipartFile file, String userId, String groupId) throws IOException, org.apache.tomcat.util.http.fileupload.FileUploadException {
        try {
            // 1. 获取上传许可（控制并发）
            uploadSemaphore.acquire();

            // 2. 验证文件（类型、大小等）
            validateFile(file);

            // 3. 检查磁盘空间是否足够
            storageMonitor.checkStorageAvailable(file.getSize());

            // 4. 创建文件信息对象
            FileInfo fileInfo = createFileInfo(file, groupId, userId);
            fileInfo.generateFileId(); // 生成文件ID

            // 5. 构建文件存储路径
            Path targetPath = buildStoragePath(fileInfo);

            // 6. 确保目标目录存在
            Files.createDirectories(targetPath.getParent());

            // 7. 保存文件到目标路径
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 8. 设置相对存储路径（去掉基础目录部分）
            String relativePath = targetPath.toString().substring(baseUploadDir.length() + 1);
            fileInfo.setStoragePath(relativePath);

            // 9. 设置文件类型
            fileInfo.setFileType(determineFileType(file));

            return fileInfo;

        } catch (InterruptedException | FileUploadException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
            try {
                throw new FileUploadException("上传被中断");
            } catch (FileUploadException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            // 释放信号量
            uploadSemaphore.release();
        }
    }
}