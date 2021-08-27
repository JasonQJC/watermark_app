package com.reemoon.watermark.task;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.reemoon.watermark.common.Const;

@Component
public class FileCleanTask {

	@Scheduled(cron = "0 0 0 * * ? ")//每天0点执行
//	@Scheduled(cron = "0/30 * * * * ? ")
	public void clean() throws IOException {
		Path start = FileSystems.getDefault().getPath(Const.TEM_PATH);
		Files.walk(start, FileVisitOption.FOLLOW_LINKS)
		.filter(item -> !item.toAbsolutePath().equals(start))
		.forEach(todel -> {
			try {
				Files.deleteIfExists(todel);
			} catch (DirectoryNotEmptyException e2) {
				// 忽略
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		Files.walk(start, FileVisitOption.FOLLOW_LINKS)
		.filter(item -> !item.toAbsolutePath().equals(start))
		.forEach(todel -> {
			try {
				Files.deleteIfExists(todel);
			} catch (DirectoryNotEmptyException e2) {
				// 忽略
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

}
