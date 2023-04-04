package org.ranG.grammar.YaccParser;

import java.util.ArrayList;

public class CodeBlockRet {
    Token t;
    ArrayList<CodeBlock> cbs;
    public CodeBlockRet(Token t,ArrayList<CodeBlock> cbs){
        this.cbs = cbs;
        this.t   = t;
    }

}
