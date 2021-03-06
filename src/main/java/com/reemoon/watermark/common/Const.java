package com.reemoon.watermark.common;

import org.springframework.stereotype.Component;

@Component
public class Const {
	public static final String LOGO_PATH = "static/images/";
    public static final String LOGO_FILE_NAME = "logo3.png";  // 水印图片文件名
    public static final int X = 10;    // 水印添加位置 X轴
    public static final int Y = 10;    // 水印添加位置 Y轴
    public static final float ALPHA = 0.4F; // 水印透明度
    public static final int X_INTERVAL = 300;  // 水印之间的间隔
    public static final int Y_INTERVAL = 450;
    public static final String TEM_PATH = 
    		System.getProperty("os.name").toLowerCase().contains("linux") ? 
    				"/watermark/temp" : "C:/watermark/temp";
}