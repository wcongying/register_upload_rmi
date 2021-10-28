package com.nicole.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@PropertySource("classpath:tencentCOS.properties")
public class COSutil {
    private Logger log = LoggerFactory.getLogger(COSutil.class);

    @Value("${SecretId}")
    private String SecretId;

    @Value("${SecretKey}")
    private String SecretKey;

    @Value("${regionName}")
    private String regionName;

    // bucket名需包含appid
    @Value("${bucketName}")
    private String bucketName;



    public void SimpleUploadFile( String sourceFile, String destinationFileName) {
        // 1 初始化用户身份信息(secretId, secretKey)
        // 请先在访问管理控制台中的 [API 密钥管理](https://console.cloud.tencent.com/cam/capi) 页面获取
        // APPId、SecretId、SecretKey
        COSCredentials cred = new BasicCOSCredentials(SecretId,
                SecretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);

        File localFile = new File(sourceFile);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, destinationFileName, localFile);
        // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
        putObjectRequest.setStorageClass(StorageClass.Standard);
        try {
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();
            String crc64 = putObjectResult.getCrc64Ecma();
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // 关闭客户端
        cosclient.shutdown();
    }
}
