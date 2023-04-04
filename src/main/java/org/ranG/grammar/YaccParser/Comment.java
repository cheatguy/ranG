package org.ranG.grammar.YaccParser;

public class Comment implements Token{
    String val;

    @Override
    public String originString() {
        return this.val;
    }

    @Override
    public boolean hasPreSpace() {
        return false;
    }
}
