package com.reemoon.watermark.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DownloadController {

	@RequestMapping("/static/**")
	public ResponseEntity<byte[]> download(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		String path = req.getRequestURI();
		if(StringUtils.isNotBlank(path) && path.startsWith("/static/")) {
			String physicalUploadPath = getClass().getClassLoader().getResource(RegExUtils.replaceFirst(path, "/", "")).getPath();
			File file = new File(physicalUploadPath);
			HttpHeaders httpHeaders = new HttpHeaders();
	        httpHeaders.add("Access-Control-Expose-Headers", "Content-Disposition");
	        httpHeaders.add("Content-Disposition",
	            "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
	        long length = file.length();
	        byte[] fileByteArray = FileUtils.readFileToByteArray(file);
			return ResponseEntity.ok().headers(httpHeaders).contentLength(length).contentType(MediaType.APPLICATION_OCTET_STREAM)
		            .body(fileByteArray);
		}
		return null;
	}
	
}
