package com.y4j.utils;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 删除Maven jar的缓存文件 jar的缓存文件会导致jar无法加载进项目
 */
public class RemoveLastUpdate {
	
	private static final Logger logger = LoggerFactory.getLogger(RemoveLastUpdate.class);


	public static void main(String[] args) {
			Configurator.initialize("log4j2.xml",
					System.getProperty("user.dir") + File.separator + "log4j2.xml");
			findFile("E:\\maven\\repository\\3lib\\advancedcruse\\1.0");

	}

	/**
	 * 解决maven jar包存在 但引用失败的问题
	 * 修改_maven.repositories、_remote.repositories的私服路径为空
	 * 删除更新缓存文件
	 * @param rootPath maven 仓库路径 只能是绝对路径{@link Files}不识别相对路径
	 */
	public static void findFile(String rootPath) {
		File[] files = new File(rootPath).listFiles();
		Objects.requireNonNull(files);
		String name;
		Path path;
		for (File f : files) {
			if (f.isDirectory()) {
				findFile(f.getPath());
			}else {
				name = f.getName();
				logger.info("name:{}", name);
				if (name.equals("_maven.repositories") || name.equals("_remote.repositories")) {
					try {
						path = Paths.get(f.getParent(), name);
						String s = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
						s = s.replaceAll("local-nexus", "");
						Files.write(path, s.getBytes());
					}catch (Exception e) {
						logger.error("", e);
					}
				}else if (name.contains("lastUpdated")) {
					if (f.delete()) {
						logger.info("删除文件:{}", name);
					}
				}
			}
		}
	}

}
