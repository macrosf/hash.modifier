package cn.oss.hash.modifier;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
    	//String rootPath = "D:\\ToImage\\";
    	String rootPath = "E:\\Upload\\";
    	FileProcesser processer = new FileProcesser(rootPath);
    	processer.processFolder();
    }
}
