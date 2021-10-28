package com.nicole.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Component
@PropertySource("classpath:aliOSS.properties")
public class OSSUtil {
    private Logger log = LoggerFactory.getLogger(OSSUtil.class);
    /**
     * 阿里密钥
     */
    @Value("${aliAccessKeyId}")
    private String aliAccessKeyId;
    @Value("${aliAccessKeySecret}")
    private String aliAccessKeySecret;
    /**
     * 阿里图片上传
     */
    @Value("${aliEndpoint}")
    private String aliEndpoint;

    @Value("${aliBucketName}")
    private String aliBucketName;

    @Value("${aliDirectory}")
    private String aliDirectory;

    /**
     * 上传文件到阿里云OSS
     *
     * @param file
     * @return
     * @throws ImgException
     */
    public String uploadImageOSS(MultipartFile file) throws ImgException {
        if (file.getSize() > 20480) {
            throw new ImgException("上传图片大小不能超过20M！");
        }
        // 获取上传的文件名全称
        String Filename = file.getOriginalFilename();
        // 获取上传文件的后缀名,并改成小写
        String suffix = Filename.substring(Filename.lastIndexOf(".")).toLowerCase();

        // 使用 UUID 给图片重命名，并去掉四个“-”。OSS并没有强制把随机数之间4个-去掉
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;

        try {
            InputStream inputStream = file.getInputStream();
            this.uploadFileOSS(inputStream, newFileName);
            return newFileName;
        } catch (Exception e) {
            throw new ImgException("图片上传失败");
        }
    }

    /**
     * 上传到阿里云OSS服务器  如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFileOSS(InputStream instream, String fileName) {
        OSS ossClient = new OSSClientBuilder().build(aliEndpoint, aliAccessKeyId, aliAccessKeySecret);
        String ret = "";
        try {
            //创建上传Object的Metadata,这是用户对object的描述
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            //设置文件类型
            objectMetadata.setContentType(getcontentType(fileName.substring(
                    fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            //上传文件
            PutObjectResult putResult = ossClient.putObject(aliBucketName,
                    aliDirectory + fileName, instream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            //关闭资源
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ossClient.shutdown();
        return ret;
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return String
     */
    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }

    /**
     * 获得图片路径
     *
     * @param fileUrl
     * @return
     */
    public String getImageUrl(String fileUrl) {
        if (!StringUtils.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            return this.getUrl(this.aliDirectory + split[split.length - 1]);
        }
        return null;
    }

    /**
     * 获得OSS的url链接
     *
     * @param key 上传的文件
     * @return
     */
    public String getUrl(String key) {

        OSS ossClient = new OSSClientBuilder().build(aliEndpoint, aliAccessKeyId, aliAccessKeySecret);
        //可以将生成的签名URL提供给访客进行临时访问。生成签名URL时，您可以通过指定URL的过期时间来限制访客的访问时长。
        // 签名URL的默认过期时间为3600秒，最大值为32400秒。设置签名URL过期时间为3600秒（1小时）。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(aliBucketName, key, expiration);
        if (url != null) {
            ossClient.shutdown();
            return url.toString();
        }
        return null;
    }

}
