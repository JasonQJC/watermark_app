package com.reemoon.watermark.model.dto;
public class ImageInfo {

    private String logoImageUrl;  // 添加了水印后的文件的 URL相对地址
    private String fileName;
    
    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public void setLogoImageUrl(String logoImageUrl) {
        this.logoImageUrl = logoImageUrl;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}