package com.sky.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储工具类
 */
@Data
@AllArgsConstructor
@Slf4j
public class LocalFileUtil {

    // 本地存储根路径（从配置文件注入）
    private String basePath;

    // 前端访问基础URL（从配置文件注入）
    private String baseUrl;

    /**
     * 上传文件到本地服务器
     * @param bytes 文件字节数组
     * @param originalFilename 原始文件名（用于获取文件后缀）
     * @return 前端可访问的文件URL
     */
    public String upload(byte[] bytes, String originalFilename) {
        try {
            // 1. 按日期创建子目录（格式：yyyy/MM/dd）
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path dirPath = Paths.get(basePath, dateDir);

            // 自动创建不存在的目录（包括多级目录）
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 2. 生成唯一文件名（避免重名覆盖）
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 3. 保存文件到本地磁盘
            Path filePath = dirPath.resolve(fileName);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(bytes);
            }

            // 4. 构建前端可访问的完整URL
            // 处理URL中的路径分隔符，统一使用正斜杠
            String fileUrl = baseUrl + dateDir.replace("\\", "/") + "/" + fileName;
            log.info("文件已保存至本地: {}, 访问URL: {}", filePath, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除本地文件
     * @param fileUrl 文件访问URL
     * @return 是否删除成功
     */
    public boolean delete(String fileUrl) {
        try {
            // 从URL中提取相对路径（去掉baseUrl部分）
            String relativePath = fileUrl.replace(baseUrl, "");
            // 拼接本地文件完整路径
            Path filePath = Paths.get(basePath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("本地文件已删除: {}", filePath);
                return true;
            }
            log.warn("要删除的文件不存在: {}", filePath);
            return false;
        } catch (Exception e) {
            log.error("删除本地文件失败", e);
            return false;
        }
    }
}
