package org.ranG.grammar.YaccParser;

public class Terminal implements Token{
    String val;
    CommonAttr attr;
    public Terminal(String val,CommonAttr attr){
        this.val = val;
        this.attr = attr;
    }
    @Override
    public String originString() {
        return val;
    }

    @Override
    public boolean hasPreSpace() {
        return this.attr.hasPreSpace;
    }
}
