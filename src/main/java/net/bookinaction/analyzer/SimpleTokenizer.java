package net.bookinaction.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

import net.bookinaction.model.Token;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements ITokenizer {

    Analyzer tokenizer;

    public SimpleTokenizer() {
        tokenizer = new SimpleAnalyzer(Version.LUCENE_4_9);
    }

    public List<Token> getTokens(String text) throws IOException {

        if (tokenizer == null)
            tokenizer = new SimpleAnalyzer(Version.LUCENE_4_9);

        List<Token> tokenList = new ArrayList<Token>();

        TokenStream tokenStream = tokenizer.tokenStream("contents", new StringReader(text));

        OffsetAttribute offsetAtt = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();

        while (tokenStream.incrementToken()) {
            String term = termAtt.toString();
            tokenList.add(new Token(term, offsetAtt.startOffset(), offsetAtt.endOffset()));
        }

        tokenStream.end();
        tokenStream.close();

        return tokenList;
    }
    
    public List<Token> getTokens(String text, String delimeters) throws IOException {
        List<Token> tokenList = new ArrayList<Token>();

        return tokenList;
    	
    }
}
