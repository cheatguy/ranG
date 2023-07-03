package org.ranG.grammar.YaccParser;

public class Operator implements Token{
    String val;
    public Operator(String val){
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
