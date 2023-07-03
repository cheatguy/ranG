package org.ranG.grammar.YaccParser;

public class Comment implements Token{
    String val;
    public Comment (String val){
        this.val = val;
    }

    @Override
    public String originString() {
        return this.val;
    }

    @Override
    public boolean hasPreSpace() {
        return false;
    }
}
