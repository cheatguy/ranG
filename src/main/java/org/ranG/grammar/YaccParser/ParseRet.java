package org.ranG.grammar.YaccParser;

import java.util.ArrayList;
import java.util.HashMap;

/* grammar 部分的parser ，外部parser */
public class ParseRet {
    ArrayList<CodeBlock> cbs;
    ArrayList<Production> pds;
    HashMap<String,Production> mp;
}
