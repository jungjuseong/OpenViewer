package net.bookinaction.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
//import org.apache.lucene.analysis.ja.JapaneseAnalyzer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

import net.bookinaction.model.Token;

public class LanguageAnalyzer {
	
	private Analyzer lucene_analyzer;

	public LanguageAnalyzer(String language) {
				
		if (true == language.equals("JP")) {
			lucene_analyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
		}
		else if (true == language.equals("KO")) {
			lucene_analyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
		}
		else if (true == language.equals("EN")) {
			lucene_analyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
		}
		else if (true == language.equals("CN")) {
			lucene_analyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
		}
		else 
			lucene_analyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
	}
	
	public List<Token> analyze(String text) {

		List<Token> tokenList = new ArrayList<Token>();

		try {

			TokenStream tokenStream = lucene_analyzer.tokenStream("contents", new StringReader(text));

			OffsetAttribute offsetAtt = tokenStream.addAttribute(OffsetAttribute.class);
			CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
			
			tokenStream.reset();

			while (tokenStream.incrementToken()) {
				String term = termAtt.toString();
						        			       
				tokenList.add(new Token(term, offsetAtt.startOffset(), offsetAtt.endOffset()));

	        }
			tokenStream.end();
			tokenStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		lucene_analyzer.close();
		
		return tokenList;
	}
	
}
