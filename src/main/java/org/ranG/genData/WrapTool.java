package org.ranG.genData;


import java.util.ArrayList;

public class WrapTool {
    static String wrapInDml(String pk, ArrayList<String> data){
        StringBuilder buf = new StringBuilder();
        buf.append(("(" + pk));
        for(String s:data){
            buf.append((","+s));
        }
        buf.append((")"));
        return buf.toString();

    }
    static String wrapInInsert(String tableName, ArrayList<String> valuesStmts){
        String insertTmp = "insert into %s values %s";
        return String.format(insertTmp,tableName,String.join(",",valuesStmts));
    }
}
