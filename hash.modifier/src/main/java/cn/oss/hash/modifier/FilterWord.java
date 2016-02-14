package cn.oss.hash.modifier;

import java.util.HashMap;
import java.util.Map;

public class FilterWord {

	public final static String JAM_CHAR = "-";
	
	private final static String wordsList[] = {
		"sex", "Sex", "SEX",
		"fuck", "Fuck", "FUCK"
	};
	
	private static Map<String, String> sensitiveWords;
	
	//构造函数里初始化替换敏感词的map，在敏感词的字母中间插入“-”
	//private FilterWord() {
	static {
		sensitiveWords = new HashMap<String, String>();
		for(int i=0; i<wordsList.length; i++) {
			String word = wordsList[i];
			StringBuffer sb = new StringBuffer();
			for (int j=0; j<word.length(); j++) {
				sb.append(word.charAt(j)+JAM_CHAR);
			}
			sensitiveWords.put(word, sb.toString());
		}
	}
	
//	public static boolean isSensitiveWord(String word) {
//		return sensitiveWords.containsKey(word);
//	}
	
	public static String[] getSensitiveWordList() {
		return wordsList;
	}
	
	public static String getJammedWord(String word) {
		return sensitiveWords.get(word);
	}
}
