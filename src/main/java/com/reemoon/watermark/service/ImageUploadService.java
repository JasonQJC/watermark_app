package com.reemoon.watermark.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUploadService {
	/**
	 * 功能：上传图片
	 * @param file 文件
	 * @param uploadPath 服务器上上传文件的路径
	 * @param physicalUploadPath  服务器上上传文件的物理路径
	 * @return 上传文件的 URL相对地址
	 */
	public String uploadImage( MultipartFile file, String uploadPath, String physicalUploadPath ) {
	
	    String filePath = physicalUploadPath + File.separator + file.getOriginalFilename();
	
	    try {
	        File targetFile=new File(filePath);
	        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return uploadPath + file.getOriginalFilename();
	}
}

