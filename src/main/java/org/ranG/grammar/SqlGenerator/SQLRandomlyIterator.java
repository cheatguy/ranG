package org.ranG.grammar.SqlGenerator;

import org.luaj.vm2.LuaValue;
import org.ranG.genData.KeyFun;
import org.ranG.grammar.YaccParser.IToken;
import org.ranG.grammar.YaccParser.Production;

import java.util.HashMap;

public class SQLRandomlyIterator {
    String productionName;
    HashMap<String, Production> productionMap;
    KeyFun keyFun;
    LuaValue luaV;
    StringBuilder printBuf;
    /* path info*/
    public PathInfo pathInfo;
    int MaxRecursive;

    public PathInfo getPathInfo(){
        return this.pathInfo;
    }
    public void clearPath(){
        this.pathInfo.clear();
    }

    public int visit(SQLVisitor visitor){

        /* 每次使用wraper需要把pathInfo clear() */
        SQLVisitor wrapper = new SQLVisitor() {
            @Override
            public boolean func(String sql) {
                 boolean res = visitor.func(sql);
                clearPath();
                return res;
            }
        }
    }



}
