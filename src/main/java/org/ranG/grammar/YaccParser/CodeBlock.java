package org.ranG.grammar.YaccParser;

public class CodeBlock implements Token{
    String val;
    CommonAttr attr;
    public CodeBlock(CommonAttr attr,String val){
        this.attr = attr;
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
