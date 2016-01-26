package net.bookinaction.utils;

// get path of the dot pattern image
public class DotPattern {
	
	static final String A3 = "A3";
	static final String A4 = "A4";
	static final String B5 = "B5";
	static final String B4 = "B4";

	static String dotPatternRoot = "/net/bookinaction/utils/res/dotpattern/";
	 
	public static String getPath(String paperSize, int page) {	 
		 assert(paperSize.equals(A4) || paperSize.equals(A3) || paperSize.equals(B4) || paperSize.equals(B5) );		 
		 
		 String resourcePath = String.format(dotPatternRoot + paperSize + "/%04d_pbh.png", page);
		 return DotPattern.class.getResource(resourcePath).toExternalForm();
	}
}
