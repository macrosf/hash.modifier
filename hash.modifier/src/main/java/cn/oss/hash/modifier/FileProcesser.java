package cn.oss.hash.modifier;

import java.io.File;
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

	public final static String BAK_PATH_APPENDIX = ".bak"+File.separator;
	public final static String SPLITTER = "|";
	private String rootPath;
	private String srcRelativePath="";
	private int folderLevel=0;
	//private String destRelativePath;
	
	public FileProcesser(String rootPath) {
		setRootPath(rootPath);
	}

	public FileProcesser(
			String rootPath,
			String srcRelativePath,
			int folderLevel) {
		setRootPath(rootPath);
		setSrcRelativePath(srcRelativePath);
		setFolderLevel(folderLevel);
	}
	
	//处理文件夹（遍历子文件夹，处理子文件）
	/**
	 * 递归函数，处理文件夹（遍历子文件夹，处理子文件）
	 * 处理目标目录为：rootPath + srcRelativePath
	 * 遇到子文件夹时，修改成员变量srcRelativePath;
	 * 遇到子文件时，调用processFile
	 * 
	 */
	public void processFolder() {
		int spacesLength = folderLevel*4;
		String spaces = CharBuffer.allocate(spacesLength).toString()
				.replace('\0', ' ');
			
		String info = String.format("%s|--Processing folder [%s]..."
				+"(rootPath:[%s]; srcRelativePath:[%s])\n", 
				spaces, rootPath+srcRelativePath, rootPath, srcRelativePath);
		
		System.out.print(info);
		
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
								rootPath, subRelativePath, folderLevel+1);
				
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
	public void processFile(String fileName) {
		int spacesLength = (folderLevel+1)*4;
		String spaces = CharBuffer.allocate(spacesLength).toString()
				.replace('\0', ' ');
		
		//去除rootPath路径尾部的'\'
		String trimedRootPath = rootPath.substring(0, rootPath.length()-2);
		
		//判断文件名中是否含有中文，如果有中文，则每个中文字后面加'|'
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<fileName.length(); i++) {
			String aChar = fileName.substring(i, i+1);
			if (Pattern.matches("[\u4E00-\u9FA5]", aChar)){
				sb.append(aChar).append(SPLITTER);
			}
			else {
				sb.append(aChar);
			}
		}
		String newFileName = sb.toString();
		
		String info = String.format("%s|--Processing file [%s]..."
				+"(rootPath:[%s]; srcRelativePath:[%s]; "
				+"destPath:[%s], "
				+"destFileName:[%s])\n", 
				spaces, fileName, 
				rootPath, srcRelativePath,
				trimedRootPath+BAK_PATH_APPENDIX+srcRelativePath,
				newFileName);	
		System.out.print(info);
		
		
	}

	private String rename(String srcFileName) {
		//TODO
		return "";
	}
	
	private void appendScramblerBytes(String fullPathName) {
		
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

	
}
