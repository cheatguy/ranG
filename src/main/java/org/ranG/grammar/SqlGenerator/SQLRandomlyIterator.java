package org.ranG.grammar.SqlGenerator;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
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
        // todo :while part
        int visitCnt = 0;
        while(true){
            /* 既然没有管他的error，那么就不进行判断了*/
            RetgenerateSQL ret = this.generateSQLRandomly(this.productionName,new LinkedMap(),sqlBuffer,false,wrapper);
            sqlBuffer.setLength(0);
            if(ret.error != "" && !ret.error.equals("normalStop")){
                return -1;
            }

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
    /* frequently used ,may has problems */
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
                // terminal
                //ignore print debug

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

                if(handlePreSpace(firstWrite,parentPreSpace,item,sqlBuffer) < 0){
                    return new RetgenerateSQL(!firstWrite,"handle space err");
                }
                /* not sure about this */
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
                this.globals.load(luaStr).call();
                if(this.printBuf.toString().length() > 0 ){
                    sqlBuffer.append(this.printBuf.toString());
                    this.printBuf.setLength(0);
                    firstWrite = false;
                }
            }else{
                //nonTerminal
                RetgenerateSQL hasSubWrite;
                if(firstWrite){
                    hasSubWrite = this.generateSQLRandomly(item.originString(),recurCounter,sqlBuffer,parentPreSpace,visitor);
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
    public class MyJavaObject {
        public String sayHello() {
            return "Hello from Java!";
        }
    }

    /*这里没有注册 */
    void registerKeyFun(Globals l,KeyFun keyfunc){

        for(Map.Entry<String, IFunc> entry : keyfunc.funcMap.entrySet()){
            String funName = entry.getKey();
            IFunc function = entry.getValue();
        }


    }
    public  SQLIterator generateSQL(ArrayList<CodeBlock> headCodeBlocks,HashMap<String,Production> productionMap,KeyFun keyfunc,String ProductionName,int maxRecursive){
        Globals l = JsePlatform.standardGlobals();
        registerKeyFun(l,keyfunc);
        /* run head code block */
        for(CodeBlock  codeblcok:headCodeBlocks){
            String luaStr = codeblcok.originString().substring( 1 ,codeblcok.originString().length() - 1);
        }
        StringBuilder pBuf = new StringBuilder();

        // cover origin lua print
        //todo 这部分我暂时忽略了Lua中print的设置
//        MyJavaObject javaFunction = new MyJavaObject();
//        l.set("print", javaFunction);

        return new SQLRandomlyIterator(productionName,productionMap,keyFun,l,pBuf,maxRecursive,new PathInfo());

    }


}
