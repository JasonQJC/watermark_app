package com.reemoon.watermark.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.reemoon.watermark.common.Const;
import com.reemoon.watermark.model.dto.ImageInfo;
import com.reemoon.watermark.service.ImageUploadService;
import com.reemoon.watermark.service.WatermarkService;

@RestController
@RequestMapping("convert")
public class WatermarkController {

	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired
	private WatermarkService watermarkService;

	@RequestMapping(value = "/a", method = RequestMethod.POST)
	public ImageInfo watermarkTest(@RequestParam("file") MultipartFile multipartFile) throws FileNotFoundException {
		String randomAlphabetic = RandomStringUtils.randomAlphabetic(1, 6);
		String relativePath = Const.TEM_PATH  + File.separator + randomAlphabetic;
		File tempDir = new File(relativePath);
		if(!tempDir.exists()) {
			tempDir.mkdirs();
		}
		String physicalUploadPath = tempDir.getAbsolutePath();
		if (multipartFile != null) {
			String originalFilename = multipartFile.getOriginalFilename();
			ImageInfo imgInfo = new ImageInfo();
			//String imageURL = 
			imageUploadService.uploadImage(multipartFile, relativePath, physicalUploadPath);
			File imageFile = new File(physicalUploadPath + File.separator + multipartFile.getOriginalFilename());
			if (StringUtils.endsWithAny(originalFilename, ".png", ".PNG", ".jpg", ".JPG", ".jpeg", ".JPEG")) {
				try {
					Pair<String, String> result = watermarkService.watermarkAdd(imageFile,
							multipartFile.getOriginalFilename(), relativePath, physicalUploadPath);
					imgInfo.setLogoImageUrl(result.getLeft().replace(Const.TEM_PATH, "static"));
					imgInfo.setFileName(result.getRight());
					return imgInfo;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else if (StringUtils.endsWithAny(originalFilename, ".pdf", ".PDF")) {
				try {
					Pair<String, String> result = watermarkService.watermarkAddPDF(imageFile,
							multipartFile.getOriginalFilename(), relativePath, physicalUploadPath);
					imgInfo.setLogoImageUrl(result.getLeft().replace(Const.TEM_PATH, "static"));
					imgInfo.setFileName(result.getRight());
					return imgInfo;
				} catch (IOException | DocumentException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}
	
}
