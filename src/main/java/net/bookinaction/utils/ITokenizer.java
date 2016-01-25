package net.bookinaction.utils;


import java.io.IOException;
import java.util.List;

import net.bookinaction.model.Token;

public interface ITokenizer {
    public List<Token> getTokens(String text) throws IOException;
}