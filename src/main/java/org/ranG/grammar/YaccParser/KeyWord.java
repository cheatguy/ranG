package org.ranG.grammar.YaccParser;

public class KeyWord implements Token{
    String val;
    CommonAttr attr;
    @Override
    public String originString() {
        return val;
    }

    @Override
    public boolean hasPreSpace() {
        return this.attr.hasPreSpace;
    }
}
