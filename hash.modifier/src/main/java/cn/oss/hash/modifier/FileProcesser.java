package cn.oss.hash.modifier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.regex.Pattern;

/*
 * TODO
 * 1.当文件夹的名字里面也包含敏感词的时候，添加分隔符
 * 2.入力文件夹，敏感词，分隔符外派配置；jar文件独立运行
 * 3.添加日志，显示处理进度
 */

/**
 * 主要功能：
 * 	1.遍历目录
 * 	2.变更文件名（中文及敏感词中间添加'|'）
 *  3.文件末尾添加扰码(scrambler)
 * 
 * @author mlxia
 *
 */
public class FileProcesser {

	public final static String BAK_PATH_APPENDIX = ".hash"+File.separator;
	private String rootPath;
	private String srcRelativePath = "";
	private String destFullPath = "";
	private int folderLevel = 0;
	//private String destRelativePath;
	private App app=null;	
	
	public FileProcesser(String rootPath, App app) {
		setRootPath(rootPath);
		setApp(app);
	}

	public FileProcesser(
			String rootPath,
			String srcRelativePath,
			int folderLevel,
			App app) {
		setRootPath(rootPath);
		setSrcRelativePath(srcRelativePath);
		setFolderLevel(folderLevel);
		setDestFullPath();
		setApp(app);
	}	
	
	public String getDestFullPath() {
		return destFullPath;
	}

	public void setDestFullPath(String destFullPath) {
		this.destFullPath = destFullPath;
	}

	private void setDestFullPath() {
		//去除rootPath路径尾部的'\'
		String trimedRootPath = rootPath.substring(0, rootPath.length()-1);		
		//设定目标文件夹全路径
		String destPath = 
				trimedRootPath
				+ BAK_PATH_APPENDIX
				+ renameFileName(srcRelativePath);
		setDestFullPath(destPath);
	}

	private void createFolderIfNotExist(String fullPath) {
		File destSubFolder = new File(fullPath);
		if (!destSubFolder.exists()) {
			destSubFolder.mkdirs();
		}	
	}
	
	//处理文件夹（遍历子文件夹，处理子文件）
	/**
	 * 递归函数，处理文件夹（遍历子文件夹，处理子文件）
	 * 处理目标目录为：rootPath + srcRelativePath
	 * 遇到子文件夹时，修改成员变量srcRelativePath;
	 * 遇到子文件时，调用processFile
	 * @throws IOException 
	 * 
	 */
	public void processFolder() throws IOException {
		int spacesLength = folderLevel*4;
		//为了做成树形的样子，生成指定长度的空格字符串
		String spaces = CharBuffer.allocate(spacesLength).toString()
				.replace('\0', ' ');
			
		String info = String.format("%s|--Processing folder [%s]..."
				+"(rootPath:[%s]; srcRelativePath:[%s])", 
				spaces, rootPath+srcRelativePath, rootPath, srcRelativePath);
		
		System.out.println(info);
		
		//处理文件夹的计数器+1
		app.incFolderProcessed();
		
		File dir = new File(rootPath+srcRelativePath);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		
		for (int i=0; i<files.length; i++) {
			File file = files[i];
			
			//如果是文件夹
			if (file.isDirectory()) {
				//取得目录最后一段，添加到srcRelativePath后面
				String subRelativePath = 
						srcRelativePath + file.getName()+File.separator;
				FileProcesser subFolderProcesser = 
						new FileProcesser(
								rootPath, subRelativePath, folderLevel+1, app);
				
//				//判断当前目录的备份目录是否已经存在，如果不存在，先创建当前目录的备份目录
//				File destFolder = new File(getDestFullPath());
//				if (!destFolder.exists()) {
//					destFolder.mkdir();
//				}
				
				//创建备份目录
				String destFullPath = subFolderProcesser.getDestFullPath();
				createFolderIfNotExist(destFullPath);
				
				//对子文件夹递归调用处理文件夹的方法
				subFolderProcesser.processFolder();
			}
			//如果是文件
			else {
				processFile(file.getName());
			}
		}

	}

	//处理文件（修改文件名，文件末尾添加扰码）
	//源文件路径=rootPath + srcRelativePath
	//目标文件路径=rootPath + BAK_PATH_APPENDIX + srcRelativePath
	public void processFile(String fileName) throws IOException {
		int spacesLength = (folderLevel+1)*4;
		String spaces = CharBuffer.allocate(spacesLength).toString()
				.replace('\0', ' ');
		
//		//去除rootPath路径尾部的'\'
//		String trimedRootPath = rootPath.substring(0, rootPath.length()-2);
		
		//判断文件名中是否含有中文，如果有中文，则每个中文字后面加'-'
//		StringBuffer sb = new StringBuffer();
//		for (int i=0; i<fileName.length(); i++) {
//			String aChar = fileName.substring(i, i+1);
//			if (Pattern.matches("[\u4E00-\u9FA5]", aChar)){
//				sb.append(aChar).append(FilterWord.JAM_CHAR);
//			}
//			else {
//				sb.append(aChar);
//			}
//		}
//		String newFileName = sb.toString();
//		newFileName = renameSensitiveWord(newFileName);
		String newFileName = renameFileName(fileName);
		
		//处理文件的计数器+1
		app.incFileProcessed();
		
		String info = String.format("%s|--Processing file [%s]..."
				+"(rootPath:[%s]; srcRelativePath:[%s]; "
				+"destPath:[%s], "
				+"destFileName:[%s])", 
				spaces, fileName, 
				rootPath, srcRelativePath,
				getDestFullPath(),
				newFileName);	
		System.out.println(info);
		
		//创建备份目录（如果不存在）
		createFolderIfNotExist(getDestFullPath());
		
		//备份文件
		File srcFile = new File(rootPath + srcRelativePath + fileName);
		File destFile = new File(getDestFullPath() + newFileName);
		if (destFile.exists()) {
			destFile.delete();
		}
		else {
			destFile.createNewFile();
		}
		
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(srcFile));
			out = new BufferedOutputStream(new FileOutputStream(destFile));
			
			int len = -1;
			byte[] b = new byte[1024];
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			
			//目标文件末尾添加1kB的空字符
			b = new byte[1024];
			out.write(b);
		}
		finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	//根据源文件/目录名，生成目标文件/目录名
	//1.中文名中间添加‘-’符号
	//2.敏感词字母之间添加‘-’符号
	private String renameFileName(String srcFileName) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<srcFileName.length(); i++) {
			String aChar = srcFileName.substring(i, i+1);
			if (Pattern.matches("[\u4E00-\u9FA5]", aChar)){
				sb.append(aChar).append(FilterWord.JAM_CHAR);
			}
			else {
				sb.append(aChar);
			}
		}
		String newFileName = sb.toString();
		return renameSensitiveWord(newFileName);		
	}
	
	//重命名带敏感词的文件名
	private String renameSensitiveWord(String srcFileName) {
		String destFileName = srcFileName;
		String[] sensitiveWords = FilterWord.getSensitiveWordList();
		
		for (int i=0; i<sensitiveWords.length; i++) {
			String sensitiveWord = sensitiveWords[i];
			if (destFileName.contains(sensitiveWord)) {
				destFileName = destFileName.replace(
						sensitiveWord, 
						FilterWord.getJammedWord(sensitiveWord));
			}

		}
		return destFileName;
	}
	
	public String getRootPath() {
		return rootPath;
	}


	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}


	public String getSrcRelativePath() {
		return srcRelativePath;
	}


	public void setSrcRelativePath(String srcRelativePath) {
		this.srcRelativePath = srcRelativePath;
	}

	public int getFolderLevel() {
		return folderLevel;
	}

	public void setFolderLevel(int folderLevel) {
		this.folderLevel = folderLevel;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	
}
