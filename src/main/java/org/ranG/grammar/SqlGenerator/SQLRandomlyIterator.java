package org.ranG.grammar.SqlGenerator;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.*;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.ranG.genData.IFunc;
import org.ranG.genData.KeyFun;
import org.ranG.genData.LoggerUtil;
import org.ranG.genData.RetStrBool;
import org.ranG.grammar.YaccParser.*;

import java.nio.file.Path;
import java.util.*;

import static org.ranG.grammar.YaccParser.Parser.*;

public class SQLRandomlyIterator implements SQLIterator {
    static Logger log = LoggerUtil.getLogger();
    String productionName;
    HashMap<String, Production> productionMap;
    KeyFun keyFun;
    Globals globals;
    StringBuilder printBuf;
    /* path info*/
    public PathInfo pathInfo;
    int maxRecursive;

    public PathInfo pathInfo(){
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
        StringBuilder sqlBuffer = new StringBuilder();

        int visitCnt = 0;
        while(true){
            RetgenerateSQL ret = this.generateSQLRandomly(this.productionName,new LinkedMap(),sqlBuffer,false,wrapper);
            if(ret.error != "" && !ret.error.equals("normalStop")){
                return -1;
            }

            /* 这里要达到max 才会返回 wrapper.func 的真值 */
            if(ret.error.equals("normalStop") || !wrapper.func(sqlBuffer.toString())){
                return 1;
            }
            sqlBuffer.setLength(0);;
        }

    }
    /*default non param */
    public SQLRandomlyIterator(){

    }
    SQLRandomlyIterator(String productionName, HashMap<String,Production> productionMap, KeyFun keyFun, Globals l, StringBuilder printBuf, int maxRecursive, PathInfo pathInfo){
        this.productionName = productionName;
        this.productionMap = productionMap;
        this.keyFun = keyFun;
        this.globals = l;
        this.printBuf = printBuf;
        this.maxRecursive = maxRecursive;
        this.pathInfo = pathInfo;

    }
    boolean willRecursive(Seq seq,HashMap<String,Boolean> set){

        for(Token item :seq.items){
            if(isTknNonTerminal(item)&& set.containsKey(item.originString()) && set.get(item.originString())){
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
    /* frequently used ,may has problems */
    int handlePreSpace(boolean firstWrite,boolean parentSpace,Token tkn,StringBuilder writer){
        if(firstWrite){
            if(parentSpace){
                if(writeSpace(writer) < 0){
                    log.error("handlePreSpace : fail to write stringbuilder");
                    return -1;
                }
            }
//            return 1;
        }
        if(tkn.hasPreSpace()){
            if(writeSpace(writer) < 0){
                log.error("handlePreSpace : fail to write stringbuilder");
            }
        }
        return 1;
    }
    class RetgenerateSQL{

        public boolean bo;
        public String error; /* return "" means no error */
        RetgenerateSQL(boolean bo,String error){
            this.bo = bo;
            this.error = error;
        }
    }

    public RetgenerateSQL generateSQLRandomly(String productionName,LinkedMap recurCounter,StringBuilder sqlBuffer,boolean parentPreSpace,SQLVisitor visitor){
        //get root production
        if(!this.productionMap.containsKey(productionName)){
            log.error("generateSQLRandomly: production not found");
            return new RetgenerateSQL(false,"production not found");
        }
        Production production = this.productionMap.get(productionName);
        this.pathInfo.productionSet.add(production);
        // check max recursive count
        recurCounter.enter(productionName);
        if(recurCounter.m.get(productionName) > this.maxRecursive){
            log.error("generateSQLRandomly: expression recursive exceed max loop");
            return new RetgenerateSQL(false,"expression recursive exceed max loop");
        }
        HashMap <String,Boolean> nearMaxRecur = new HashMap<>();
        for (Map.Entry<String, Integer> entry : recurCounter.m.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            /*这里的max recursive 很难达到 */
            if(value.intValue() == this.maxRecursive){
                nearMaxRecur.put(key,true);
            }
        }
        ArrayList<Seq> selectableSeqs = new ArrayList<>();
        /* 这里recursive 出现了问题 */
        for(Seq seq:production.alter){
            if(!willRecursive(seq,nearMaxRecur)){
                selectableSeqs.add(seq);
            }
        }
        if(selectableSeqs.size() == 0){
            log.error("generateSQLRandomly:recursive num exceed ");
            return new RetgenerateSQL(false,"expression recursive exceed max loop");
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
                // 1. is a terminal 2. is a non-terminal and Not in the productionMap
                //semicolon
                if(item.originString().equals(";")){
                    // not last char in bnf
                    if(selectIndex  != production.alter.size() -1 || i != seqs.items.size() -1){
                        if(!visitor.func(sqlBuffer.toString())){
                            return new RetgenerateSQL(!firstWrite,"normalStop");
                        }
                        sqlBuffer.setLength(0);
                        firstWrite = true;
                        continue;
                    }else{
                        /* last char ,ignore */
                        continue;
                    }
                }
                /* 处理 subquery中的a._fieldchar 中，它只解析出了一个 a（item）
                    causing error
                 */

                if(handlePreSpace(firstWrite,parentPreSpace,item,sqlBuffer) < 0){
                    return new RetgenerateSQL(!firstWrite,"handle space err");
                }
                /* not sure about this */
//                sqlBuffer.append(isTknNonTerminal(item));
                sqlBuffer.append(item.originString());
                firstWrite = false;
            }else if(isKeyword(item)){
                if(handlePreSpace(firstWrite,parentPreSpace,item,sqlBuffer) < 0){
                    return new RetgenerateSQL(!firstWrite,"handle preSpace err");
                }
                /* keyword parse */
                RetStrBool ret = this.keyFun.gen(item.originString());
                if(ret == null){
                    return new RetgenerateSQL(!firstWrite,"fail to gen ");
                }else if(ret.bo){
                    sqlBuffer.append(ret.str);
                    firstWrite = true;

                }else{
                    log.error("generateSQLRandomly: key word not support");
                    return  new RetgenerateSQL(!firstWrite,"not support keyword");
                }

            }else if(isCodeBlock(item)){
                if(handlePreSpace(firstWrite,parentPreSpace,item,sqlBuffer) < 0 ){
                    return  new RetgenerateSQL(!firstWrite,"fail to run lua");
                }
                // lua code block
                //这originString掐头去尾
                String luaStr = item.originString().substring(1,item.originString().length() - 1);
                this.globals.load(luaStr).call(); /*执行这段lua ，可行*/
                if(this.printBuf.toString().length() > 0 ){
                    sqlBuffer.append(this.printBuf.toString());
                    this.printBuf.setLength(0);
                    firstWrite = false;
                }
            }else{
                //nonTerminal recursive
                RetgenerateSQL hasSubWrite;
                if(firstWrite){
                    /* first write meaning for ? */
                    hasSubWrite = this.generateSQLRandomly(item.originString(),recurCounter,sqlBuffer,parentPreSpace,visitor);
                    /* this got error `normalStop` */
                }else{
                    hasSubWrite = this.generateSQLRandomly(item.originString(),recurCounter,sqlBuffer,item.hasPreSpace(),visitor);
                }

                if(firstWrite && hasSubWrite.bo){
                    firstWrite = false;
                }
                if(hasSubWrite.error !=""){
                    return new RetgenerateSQL(!firstWrite,hasSubWrite.error);
                }
            }
        }


        // 最后需要执行 go中defer满足先进后出
        recurCounter.leave(productionName);
        return new RetgenerateSQL(!firstWrite,"");
    }
    // 一个内部类，用于专门给lua函数调用
    static class MyFunction extends VarArgFunction {
        String str;
        @Override
        public Varargs invoke(Varargs args) {
//            String name = args.tojstring(1);
            return LuaValue.valueOf(str);
        }
    }
    /*这里没有注册 */
    void registerKeyFun(Globals l,KeyFun keyfunc){

        for(Map.Entry<String, IFunc> entry : keyfunc.funcMap.entrySet()){
            String funName = entry.getKey();
            IFunc function = entry.getValue();
            /*call func */
            MyFunction fc = new MyFunction();
            fc.str = function.func();
            l.set(funName,fc);

        }
    }
    static class MyPrint extends VarArgFunction {
        StringBuilder sb = new StringBuilder();
        MyPrint(StringBuilder sb){
            this.sb = sb;
        }
        @Override
        public Varargs invoke(Varargs args) {
            /* 获取第一个参数 */
            String name = args.tojstring(1);
            this.sb.append(name);
            /*返回值为LuaValue空*/
            return NIL;
        }
    }
    public  SQLIterator generateSQL(ArrayList<CodeBlock> headCodeBlocks,HashMap<String,Production> productionMap,KeyFun keyfunc,String productionName,int maxRecursive){
        Globals l = JsePlatform.standardGlobals();
        /* warn： ignore it ,but for debuging */
//        registerKeyFun(l,keyfunc);
        /* run head code block ,需要去掉{}，所以index 1到size-1*/
        for(CodeBlock  codeblcok:headCodeBlocks){
            String luaStr = codeblcok.originString().substring( 1 ,codeblcok.originString().length() - 1);
            LuaValue chunk = LuaValue.valueOf(luaStr);
            chunk.call(l);
        }


        StringBuilder pBuf = new StringBuilder();
        MyPrint mp = new MyPrint(pBuf);

        l.set("print",mp);

        return new SQLRandomlyIterator(productionName,productionMap,keyfunc,l,pBuf,maxRecursive,new PathInfo());

    }


}
