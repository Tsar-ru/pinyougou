package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${uploadServer}")
    private String uploadServer;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String filename = file.getOriginalFilename();
//            filename  "we.we.T.1.jpg"
            String extName = filename.substring(filename.lastIndexOf(".")+1); //从最后一个出现.的位置切
            String fileUrl = fastDFSClient.uploadFile(file.getBytes(), extName);
//            /group1/M00/00/01/wKgZhVup_vCABMr0AACuI4TeyLI969.jpg
            return  new Result(true,uploadServer+fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"上传失败");
        }
    }
}
