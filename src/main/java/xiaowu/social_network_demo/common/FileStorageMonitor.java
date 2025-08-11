package xiaowu.social_network_demo.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xiaowu.social_network_demo.exception.FileUploadException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储空间监控服务
 * 作用：检查磁盘空间使用情况，防止磁盘写满
 */
@Service
public class FileStorageMonitor {

    // 文件上传基础目录（从配置读取）
    @Value("${file.upload.base-dir:uploads}")
    private String baseUploadDir;

    // 存储空间使用率阈值（%）
    @Value("${file.storage.threshold:90}")
    private int storageThreshold;

    /**
     * 检查存储空间是否足够
     * @param requiredSize 需要的空间大小（字节）
     * @throws FileUploadException 当空间不足时抛出
     */
    public void checkStorageAvailable(long requiredSize) throws FileUploadException {
        // 1. 获取上传目录路径
        Path uploadPath = Paths.get(baseUploadDir);

        // 2. 获取磁盘空间信息
        long totalSpace = uploadPath.toFile().getTotalSpace();   // 总空间（字节）
        long usableSpace = uploadPath.toFile().getUsableSpace(); // 可用空间（字节）

        // 3. 计算已使用空间百分比
        int usedPercent = (int) ((totalSpace - usableSpace) * 100 / totalSpace);

        // 4. 检查是否超过阈值
        if (usedPercent >= storageThreshold) {
            throw new FileUploadException(
                    String.format("存储空间不足，当前使用率%d%%，最大允许%d%%",
                            usedPercent, storageThreshold));
        }

        // 5. 检查是否有足够空间（预留2倍缓冲）
        if (usableSpace < requiredSize * 2) {
            throw new FileUploadException(
                    String.format("存储空间不足，需要%.2fMB，可用%.2fMB",
                            requiredSize / (1024.0 * 1024),
                            usableSpace / (1024.0 * 1024)));
        }
    }
}