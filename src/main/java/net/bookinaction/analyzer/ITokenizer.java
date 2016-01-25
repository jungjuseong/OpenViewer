package net.bookinaction.analyzer;

import java.io.IOException;
import java.util.List;

public interface ITokenizer {
    public List<net.bookinaction.model.Token> getTokens(String text) throws IOException;
}