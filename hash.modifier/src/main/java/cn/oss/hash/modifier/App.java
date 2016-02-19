package cn.oss.hash.modifier;

import java.io.IOException;


/**
 * Hello world!
 *
 */
public class App 
{
	private int fileProcessed = 0;
	private int folderProcessed = 0;
	
    public static void main( String[] args ) throws IOException
    {
        //System.out.println( "Hello World!" );
    	//String rootPath = "D:\\ToImage\\";
    	//String rootPath = "G:\\03.115_NetDisk\\我的接收\\离线下载.upload\\";
    	String rootPath = "G:\\03.115_NetDisk\\我的接收\\download\\";
    	App app = new App();
    	FileProcesser processer = new FileProcesser(rootPath, app);
    	
    	long start = System.currentTimeMillis();
    	processer.processFolder();
    	long end = System.currentTimeMillis();
    	long seconds = (end-start)/1000;
    	long hour = seconds/3600;
    	long minute = (seconds%3600)/60;
    	long second = (seconds%3600)%60;
    	
		String info = String.format("====ALL DONE===\n"
				+"====time elapsed: %d hours, %d minutes, %d secondes\n"
				+"====folder processed: %d, file processed: %d.",
				hour, minute, second,
				app.getFolderPorcessed(), app.getFileProcessed());
		
		System.out.println(info);		    	
    }

    public void incFileProcessed() {
    	fileProcessed++;
    }
    
    public void incFolderProcessed() {
    	folderProcessed++;
    }
    
	public int getFileProcessed() {
		return fileProcessed;
	}

	public void setFileProcessed(int fileProcessed) {
		this.fileProcessed = fileProcessed;
	}

	public int getFolderPorcessed() {
		return folderProcessed;
	}

	public void setFolderPorcessed(int folderPorcessed) {
		this.folderProcessed = folderPorcessed;
	}
}
