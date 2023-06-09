package org.ranG.grammar.YaccParser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rewrite.MapRewritePolicy;
import org.ranG.genData.LoggerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.ranG.grammar.Grammar.specialRune;

public class Parser {

    static Logger log = LoggerUtil.getLogger();
    static final int initState = 0;
    static final int delimFetchedState = 1;
    static final int termFetchedState = 2;
    static final int prepareNextProdState = 3;
    static final int endState = 4;

    /*区别于外部的parse */
    public ParseRet parseInside(IToken tk){
        Token tkn;
        ArrayList<Production> prods = new ArrayList<>();
        Production p;
        /* production serial number */
        int pNumber = 0;
        Token lastTerm = null;
        int state = initState;
        /* 收集一些lua脚本代码执行，是头部代码块 */
        /* error: 这里没有正确的skip掉 comment*/
        CodeBlockRet ret = collectHeadCodeBlocks(tk);
        if(ret == null){
            log.error("parseInside: get headCodeBlock fail");
            return null;
        }
        if(!isTknNonTerminal(ret.t)){
            log.error("parseInside: token is not a nonTerminal");
            return null;
        }
        p = new Production(ret.t,pNumber);
        pNumber++;
        Seq s = new Seq(null);
        /* initState -> delimFetchedState -> termFetchedState ->... */
        while(state != endState){
            tkn = skipComment(tk);

            if(tkn == null){
                log.error("parseInside: not reach end state");
                return null;
            }
            switch (state) {
                case initState :{
                    if(!Objects.equals(tkn.originString(), ":")){
                        log.error("parseInside:expect :");
                        return null;
                    }
                    state = delimFetchedState;
                    break;
                }
                case delimFetchedState : {
                    if(isEOF(tkn)){
                        s.items.add(new Terminal(null,""));
                        p.AppendSeq(s);
                        prods.add(p);
                        state = endState;
                        continue;
                    }

                    if(tkn.originString().equals("|") || isEOF(tkn)){
                        s.items.add(new Terminal(null,""));
                        p.AppendSeq(s);
                        s = new Seq(null);
                    }else if(tkn.originString().equals(":")){
                        continue;
                    }else{
                        state = termFetchedState;
                        s.items.add(tkn);
                    }
                    break;
                }
                /* state after first term fetched */
                case termFetchedState : {
                    /* 读入的是 CREATE TABLE 这种串 */
                    if(tkn instanceof Eof){
                        p.AppendSeq(s);
                        prods.add(p);
                        state = endState;
                    }else if(tkn instanceof Operator){
                        if(tkn.originString().equals("|")){
                            p.AppendSeq(s);
                            s = new Seq(null);
                        }
                        if(tkn.originString().equals(":")){
                            /* 这里解析存在一点问题，BNF中有|后面为空的式子会解析不到下一个bnf的头 */
                            Terminal t = new Terminal(null,"");
                            s = new Seq(new ArrayList<>());
                            s.items.add(t);
                            p.AppendSeq(s);
                            prods.add(p);
                            p = new Production(s.items.get(0),pNumber);
                            pNumber++;
                            s = new Seq(null);
                        }
                        state = delimFetchedState;
                    }else if(tkn instanceof NonTerminal || tkn instanceof KeyWord || tkn instanceof Terminal || tkn instanceof CodeBlock){

                        lastTerm = tkn;
                        state = prepareNextProdState;
                    }
                    break;
                }
                case prepareNextProdState:{
                    /* core part ,read structure */
                    if(tkn instanceof Eof){
                        s.items.add(lastTerm);
                        p.AppendSeq(s);;
                        prods.add(p);
                        state = endState;
                    }else if(tkn instanceof Operator){
                        if(((Operator) tkn).val.equals("|")){
                            s.items.add(lastTerm);
                            p.AppendSeq(s);
                            s = new Seq(null);
                        }else if(((Operator) tkn).val.equals(":")){
                            p.AppendSeq(s);
                            s = new Seq(null);
                            prods.add(p);
                            if(!isTknNonTerminal(lastTerm)){
                                log.error("parseInside: the char is not a nonterminal");
                                return null;
                            }
                            p = new Production(lastTerm,pNumber);
                            pNumber ++;
                        }
                        state = delimFetchedState;
                    }else if(tkn instanceof NonTerminal || tkn instanceof KeyWord || tkn instanceof Terminal || tkn instanceof CodeBlock){
                        /* potential problem */
                        s.items.add(lastTerm);
                        lastTerm = tkn;
                    }
                    break;

                }
            }
        }
        /*cbs主要是预定义的lua代码块，可能是为空的*/
        ParseRet returnV = new ParseRet(ret.cbs,prods,null);
        return returnV;

    }
    public Token skipComment(IToken tk){
        while(true){
            Token t = tk.func();
            if(t == null){
                log.error("skipComment: can't get next token");
                return null;
            }
            if(!isComment(t)){
                return t;
            }
        }
    }
    public CodeBlockRet collectHeadCodeBlocks(IToken tk){
        ArrayList<CodeBlock> cbs = new ArrayList<>();
        Token t;
        while(true){
            /*返回一个非 comment的token */
            t = skipComment(tk);
            if( t == null){
                log.error("collectHeadCodeBlocks: skip comment error");
                return null;
            }
            if( t instanceof CodeBlock){
                cbs.add((CodeBlock)t);
            }else{
                break;
            }
        }
        CodeBlockRet ret = new CodeBlockRet(t,cbs);
        return ret;
    }

    public static boolean isSpecialChar(char c){
        return specialRune.containsKey(c);
    }
    public static boolean isEOF(Token tkn){
        if(tkn instanceof Eof){
            return true;
        }
        return false;
    }
    public static boolean isComment(Token tkn){
        if(tkn instanceof Comment){
            return true;
        }
        return false;
    }
    public static  boolean isTknNonTerminal(Token tkn){
        if(tkn instanceof NonTerminal){
            return true;
        }
        return false;
    }
    public static boolean isTerminal(Token tkn){
        if(tkn instanceof Terminal){
            return true;
        }
        return false;
    }
    public static boolean isKeyword(Token tkn){
        if(tkn instanceof KeyWord){
            return true;
        }
        return false;
    }
    public static boolean isCodeBlock(Token tkn){
        if(tkn instanceof CodeBlock){
            return true;
        }
        return false;
    }



    public static boolean nonTerminalNotInMap(HashMap<String,Production> pMap,Token tkn){
        if(!(tkn instanceof NonTerminal)){
            return false;
        }

        return !pMap.containsKey((((NonTerminal)tkn).originString()));
    }

    public static boolean nonTerminalInMap(HashMap<String,Production> pMap,Token tkn){
        if(!(tkn instanceof NonTerminal)){
            return false;
        }

        return pMap.containsKey((((NonTerminal)tkn).originString()));
    }





}
