package com.reemoon.watermark.service.impl;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.reemoon.watermark.common.Const;
import com.reemoon.watermark.service.WatermarkService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@Service
public class ImgWatermarkServiceImpl implements WatermarkService {

	@Override
	public Pair<String, String> watermarkAdd(File inputFile, String inputFileName, String uploadPath,
			String realUploadPath, String logoPathParent) throws IOException {

		String fileNamePrefix = StringUtils.substringBeforeLast(inputFileName, ".");
		String fileNameStufix = StringUtils.substringAfterLast(inputFileName, ".");
		String outputFileName = fileNamePrefix + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + "."
				+ fileNameStufix;

		Image inputFileImage = ImageIO.read(inputFile);

		int inputFileImageWidth = inputFileImage.getWidth(null);
		int inputFileImageHeight = inputFileImage.getHeight(null);

		BufferedImage bufferedImage = new BufferedImage(inputFileImageWidth, inputFileImageHeight,
				BufferedImage.TYPE_INT_RGB); // 创建图片缓存对象
		Graphics2D g = bufferedImage.createGraphics(); // 创建绘绘图工具对象
		g.drawImage(inputFileImage, 0, 0, inputFileImageWidth, inputFileImageHeight, null); // 使用绘图工具将原图绘制到缓存图片对象

		
		String logoPath = logoPathParent + File.separator + Const.LOGO_FILE_NAME;
		File logoFile = new File(logoPath); // 读取水印图片
		Image logoImage = ImageIO.read(logoFile);

		int logoImageWidth = logoImage.getWidth(null);
		int logoImageHeight = logoImage.getHeight(null);

		int inputFileImageWidthD6 = inputFileImageWidth / 6;
		double scale = 1.0;
		if (logoImageWidth > inputFileImageWidthD6) {
			scale = logoImageWidth / inputFileImageWidthD6;
		}

		if (scale > 0) {
			logoImage = logoImage.getScaledInstance(inputFileImageWidthD6, (int) (logoImageHeight / scale),
					Image.SCALE_AREA_AVERAGING);
		}

		int markWidth = logoImage.getWidth(null); // 水印图片的宽度和高度
		int markHeight = logoImage.getHeight(null);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, Const.ALPHA)); // 设置水印透明度
		g.rotate(Math.toRadians(-10), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2); // 旋转图片

		int x = Const.X;
		int y = Const.Y;

		int xInterval = Const.X_INTERVAL;
		int yInterval = Const.Y_INTERVAL;

		double count = 1.5;
		while (x < inputFileImageWidth * count) { // 循环添加水印
			y = -inputFileImageHeight / 2;
			while (y < inputFileImageHeight * count) {
				g.drawImage(logoImage, x, y, null); // 添加水印
				y += markHeight + yInterval;
			}
			x += markWidth + xInterval;
		}

		g.dispose();
		try (OutputStream os = new FileOutputStream(realUploadPath + "/" + outputFileName);) {
			JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
			en.encode(bufferedImage);
		}

		return Pair.of(uploadPath + File.separator + outputFileName, outputFileName);
	}

	@Override
	public Pair<String, String> watermarkAddPDF(File inputFile, String inputFileName, String uploadPath,
			String realUploadPath, String logoPathParent) throws IOException, DocumentException {
		String fileNamePrefix = StringUtils.substringBeforeLast(inputFileName, ".");
		String fileNameStufix = StringUtils.substringAfterLast(inputFileName, ".");
		String outputFileName = fileNamePrefix + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + "."
				+ fileNameStufix;
		PdfReader pdfReader = new PdfReader(realUploadPath + File.separator + inputFileName);
		File targetFile = new File(realUploadPath + File.separator + outputFileName);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(targetFile));

		PdfGState gs = new PdfGState();
		gs.setFillOpacity(Const.ALPHA);
		gs.setStrokeOpacity(Const.ALPHA);

		com.itextpdf.text.Image logoImage = com.itextpdf.text.Image
				.getInstance(logoPathParent + File.separator + Const.LOGO_FILE_NAME);

		for (int i = 1; i < pdfReader.getNumberOfPages() + 1; i++) {
			Rectangle rectangle = pdfReader.getPageSize(i);
			PdfContentByte overContent = pdfStamper.getOverContent(i);
			overContent.setGState(gs);
			float pageWidthD6 = rectangle.getWidth() / 6;
			double scale = 1.0;
			if (logoImage.getWidth() > pageWidthD6) {
				scale = logoImage.getWidth() / pageWidthD6;
			}
			float newHeight = (float) (logoImage.getHeight() / scale);

			for (int startx = Const.X; startx < rectangle.getWidth() * 1.5; startx += pageWidthD6) {
				for (int starty = Const.Y; starty < rectangle.getHeight() * 1.5; starty += newHeight * 5) {
					logoImage.setAbsolutePosition(startx, starty);

					logoImage.scaleAbsolute(pageWidthD6, newHeight);
					logoImage.setRotationDegrees(45);
					overContent.addImage(logoImage);
				}
			}
		}
		try {
			if (pdfStamper != null) {
				pdfStamper.close();
			}
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		if (pdfReader != null) {
			pdfReader.close();
		}

		return Pair.of(uploadPath + File.separator + outputFileName, outputFileName);
	}

}
