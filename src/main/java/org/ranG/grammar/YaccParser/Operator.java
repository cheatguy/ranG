package org.ranG.grammar.YaccParser;

public class Operator implements Token{
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
