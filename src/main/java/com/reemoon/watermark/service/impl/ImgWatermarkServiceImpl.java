package com.reemoon.watermark.service.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.reemoon.watermark.common.Const;
import com.reemoon.watermark.service.WatermarkService;

@Service
public class ImgWatermarkServiceImpl implements WatermarkService {

	@Autowired
	ApplicationContext appContext;
	
	@Override
	public Pair<String, String> watermarkAdd(File inputFile, String inputFileName, String uploadPath,
			String realUploadPath) throws IOException {

		String fileNamePrefix = StringUtils.substringBeforeLast(inputFileName, ".");
		String fileNameStufix = StringUtils.substringAfterLast(inputFileName, ".");
		String outputFileName = fileNamePrefix + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + "."
				+ fileNameStufix;

		Image inputFileImage = ImageIO.read(inputFile);

		int inputFileImageWidth = inputFileImage.getWidth(null);
		int inputFileImageHeight = inputFileImage.getHeight(null);

		BufferedImage bufferedImage = new BufferedImage(inputFileImageWidth, inputFileImageHeight,
				BufferedImage.TYPE_INT_RGB); // ????????????????????????
		Graphics2D graphics2D = bufferedImage.createGraphics(); // ???????????????????????????
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	    graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	    graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
	    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.drawImage(inputFileImage, 0, 0, inputFileImageWidth, inputFileImageHeight, null); // ??????????????????????????????????????????????????????

		
		Resource logoResource = appContext.getResource("classpath:" + Const.LOGO_PATH + Const.LOGO_FILE_NAME);
		Image logoImage = ImageIO.read(logoResource.getInputStream());
		int logoImageWidth = logoImage.getWidth(null);
		int logoImageHeight = logoImage.getHeight(null);

		int inputFileImageWidthD6 = inputFileImageWidth / 3;
		double scale = 1.0;
		if (logoImageWidth > inputFileImageWidthD6) {
			scale = logoImageWidth / inputFileImageWidthD6;
		}

		if (scale > 0) {
			logoImage = logoImage.getScaledInstance(inputFileImageWidthD6, (int) (logoImageHeight / scale),
					Image.SCALE_SMOOTH);
		}

		int markWidth = logoImage.getWidth(null); // ??????????????????????????????
		int markHeight = logoImage.getHeight(null);

		graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, Const.ALPHA)); // ?????????????????????
		graphics2D.rotate(Math.toRadians(-45), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2); // ????????????
		graphics2D.setColor(Color.GRAY);
		graphics2D.setFont(new Font(null, Font.BOLD, 70));

		int x = Const.X;
		int y = Const.Y;

		int xInterval = Const.X_INTERVAL;
		int yInterval = Const.Y_INTERVAL;

		double count = 1.5;
		while (x < inputFileImageWidth * count) { // ??????????????????
			y = -inputFileImageHeight / 2;
			while (y < inputFileImageHeight * count) {
				graphics2D.drawImage(logoImage, x, y, null); // ????????????
				graphics2D.drawString("????????????,????????????", x + 150, y - 30);
				y += markHeight + yInterval;
			}
			x += markWidth + xInterval;
		}

		graphics2D.dispose();
		try (OutputStream os = new FileOutputStream(realUploadPath + "/" + outputFileName);) {
			ImageIO.write(bufferedImage, "jpg", new File(realUploadPath + "/" + outputFileName));
		}

		return Pair.of(uploadPath + File.separator + outputFileName, outputFileName);
	}

	@Override
	public Pair<String, String> watermarkAddPDF(File inputFile, String inputFileName, String uploadPath,
			String realUploadPath) throws IOException, DocumentException {
		String fileNamePrefix = StringUtils.substringBeforeLast(inputFileName, ".");
		String fileNameStufix = StringUtils.substringAfterLast(inputFileName, ".");
		String outputFileName = fileNamePrefix + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + "."
				+ fileNameStufix;
		PdfReader pdfReader = new PdfReader(realUploadPath + File.separator + inputFileName);
		File targetFile = new File(realUploadPath + File.separator + outputFileName);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(targetFile));
		BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		PdfGState gs = new PdfGState();
		gs.setFillOpacity(Const.ALPHA);
		//gs.setStrokeOpacity(Const.ALPHA);

		Resource logoResource = appContext.getResource("classpath:" + Const.LOGO_PATH + Const.LOGO_FILE_NAME);
		com.itextpdf.text.Image logoImage = com.itextpdf.text.Image.getInstance(IOUtils.toByteArray(logoResource.getInputStream()));
		for (int i = 1; i < pdfReader.getNumberOfPages() + 1; i++) {
			Rectangle rectangle = pdfReader.getPageSize(i);
			PdfContentByte overContent = pdfStamper.getOverContent(i);
			overContent.setGState(gs);
			overContent.setFontAndSize(baseFont, 60);
			overContent.setRGBColorStroke(206,203,203);
			overContent.setRGBColorFill(206,203,203);
			float pageWidthD6 = rectangle.getWidth() / 3;
			double scale = 1.0;
			if (logoImage.getWidth() > pageWidthD6) {
				scale = logoImage.getWidth() / pageWidthD6;
			}
			float newHeight = (float) (logoImage.getHeight() / scale);

			for (int startx = Const.X; startx < rectangle.getWidth() * 1.5; startx += (pageWidthD6 + Const.X_INTERVAL)) {
				for (int starty = Const.Y; starty < rectangle.getHeight() * 1.5; starty += (newHeight + Const.Y_INTERVAL)) {
					logoImage.setAbsolutePosition(startx, starty);
					overContent.showTextAligned(Element.ALIGN_LEFT, "????????????,????????????", startx + 80, starty + 175, 45);
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
