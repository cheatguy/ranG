package org.ranG.grammar.YaccParser;

/* 关键字，_开头 */
public class KeyWord implements Token{
    String val;
    CommonAttr attr;

    public KeyWord(CommonAttr attr,String val){
        this.attr =attr;
        this.val = val;
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
