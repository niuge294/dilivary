package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.LocalFileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private LocalFileUtil localFileUtil;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        try {
            log.info("文件上传：文件名={}, 大小={}字节", file.getOriginalFilename(), file.getSize());

            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 调用工具类上传文件，获取访问URL
            String fileUrl = localFileUtil.upload(file.getBytes(), file.getOriginalFilename());

            // 返回成功结果，包含文件URL
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败，请重试");
        }
    }
}
