package org.ranG.grammar.SqlGenerator;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;
import org.ranG.genData.KeyFun;
import org.ranG.genData.LoggerUtil;
import org.ranG.grammar.YaccParser.*;

import java.util.*;

import static org.ranG.grammar.YaccParser.Parser.*;

public class SQLRandomlyIterator {
    static Logger log = LoggerUtil.getLogger();
    String productionName;
    HashMap<String, Production> productionMap;
    KeyFun keyFun;
    LuaValue luaV;
    StringBuilder printBuf;
    /* path info*/
    public PathInfo pathInfo;
    int maxRecursive;

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
        };
        StringBuilder sqlBuffer;
        while(true){

        }

    }
    boolean willRecursive(Seq seq,HashMap<String,Boolean> set){
        for(Token item :seq.items){
            if(isTknNonTerminal(item) && set.get(item.originString())){
                return true;
            }
        }
        return false;
    }

    /* return -1 means error */
    int writeSpace(StringBuilder writer){
        writer.append(" "); // 写入空格
        return 1;

    }
    /* return < 0 means error */
    int handlePreSpace(boolean firstWrite,boolean parentSpace,Token tkn,StringBuilder writer){
        if(firstWrite){
            if(parentSpace){
                if(writeSpace(writer) < 0){
                    log.error("handlePreSpace : fail to write stringbuilder");
                    return -1;
                }
            }
            return 1;
        }
        if(tkn.hasPreSpace()){
            if(writeSpace(writer) < 0){
                log.error("handlePreSpace : fail to write stringbuilder");
            }
        }
        return 1;
    }
    public boolean generateSQLRandomly(String productionName,LinkedMap recurCounter,StringBuilder sqlBuffer,boolean parentPreSpace,SQLVisitor visitor){
        //get root production
        if(!this.productionMap.containsKey(productionName)){
            log.error("generateSQLRandomly: production not found");
            return false;
        }
        Production production = this.productionMap.get(productionName);
        this.pathInfo.productionSet.add(production);
        // check max recursive count
        recurCounter.enter(productionName);
        if(recurCounter.m.get(productionName) > this.maxRecursive){
            log.error("generateSQLRandomly: expression recursive exceed max loop");
            return false;
        }
        HashMap <String,Boolean> nearMaxRecur = new HashMap<>();
        for (Map.Entry<String, Integer> entry : recurCounter.m.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value.intValue() == this.maxRecursive){
                nearMaxRecur.put(key,true);
            }
        }
        ArrayList<Seq> selectableSeqs = new ArrayList<>();
        for(Seq seq:production.alter){
            if(!willRecursive(seq,nearMaxRecur)){
                selectableSeqs.add(seq);
            }
        }
        if(selectableSeqs.size() == 0){
            log.error("generateSQLRandomly:recursive num exceed ");
            return false;
        }

        // rand
        Random rand = new Random();
        int selectIndex = rand.nextInt(selectableSeqs.size()); /* len is not include */
        Seq seqs = selectableSeqs.get(selectIndex);
        this.pathInfo.seqSet.add(seqs);
        boolean firstWrite = true;
        for(int i =0;i<seqs.items.size();i++){
            Token item = seqs.items.get(i);
            if(isTerminal(item) || nonTerminalNotInMap(this.productionMap,item)){
                // terminal
                //ignore print debug

                //semicolon
                if(item.originString().equals(";")){
                    // not last char in bnf
                    if(selectIndex  != production.alter.size() -1 || i != seqs.items.size() -1){
                        if(!visitor.func(sqlBuffer.toString())){
                            return !firstWrite;
                        }
                        sqlBuffer.setLength(0);
                        firstWrite = true;
                        continue;
                    }else{
                        /* last char ,ignore */
                        continue;
                    }
                }

                if()


            }
        }


        // 最后需要执行 go中defer满足先进后出
        recurCounter.leave(productionName);
    }



}
