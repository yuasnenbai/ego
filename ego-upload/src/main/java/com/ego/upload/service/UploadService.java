package com.ego.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/4
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Slf4j
@Service
public class UploadService {

    private List<String> contentTypes = Arrays.asList("image/png", "image/jpg");

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String upload(MultipartFile file) {
        String result = null;
        try {

            //文件后缀效验(image/png,image/jpg)
            if (!contentTypes.contains(file.getContentType())) {
                log.error("文件上传类型不匹配");
                return null;
            }

            //文件类型校验
            if (ImageIO.read(file.getInputStream()) == null) {
                log.error("文件数据有错误");
                return null;
            }


            //拷贝文件c://image文件夹下
            File dir = new File("c://images");
            if (!dir.exists()) {
                dir.mkdirs();
            }

//            file.transferTo(new File("c://images/", file.getOriginalFilename()));
            //获取后缀
            String suffix = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            //上传
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);
            //拼接完整的访问路径
            result = "http://image.ego.com/"+storePath.getFullPath();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }
}
