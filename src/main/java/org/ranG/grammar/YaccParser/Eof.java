package org.ranG.grammar.YaccParser;

public class Eof implements Token{

    @Override
    public String originString() {
        return "EOF";
    }

    @Override
    public boolean hasPreSpace() {
        return false;
    }
}
