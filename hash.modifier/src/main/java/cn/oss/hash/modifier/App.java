package cn.oss.hash.modifier;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        //System.out.println( "Hello World!" );
    	//String rootPath = "D:\\ToImage\\";
    	String rootPath = "G:\\03.115_NetDisk\\我的接收\\离线下载.upload\\";
    	FileProcesser processer = new FileProcesser(rootPath);
    	processer.processFolder();
    }
}
