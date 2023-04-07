package org.ranG.grammar.YaccParser;

import org.ranG.genData.generators.Common;

public class NonTerminal implements Token{
    String val;
    CommonAttr attr;
    public NonTerminal(CommonAttr attr,String val){
        this.attr = attr;
        this.val  = val;
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
