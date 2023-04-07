package org.ranG.grammar.YaccParser;

import java.util.ArrayList;
import java.util.HashMap;

/* grammar 部分的parser ，外部parser */
public class ParseRet {
    public ArrayList<CodeBlock> cbs;
    public ArrayList<Production> pds;
    public HashMap<String,Production> mp;
    public ParseRet(ArrayList<CodeBlock> cbs,ArrayList<Production> pds,HashMap<String,Production> mp){
        this.cbs = cbs;
        this.pds = pds;
        this.mp  = mp;
    }
}
