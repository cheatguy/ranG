package org.ranG.grammar;

import org.apache.logging.log4j.Logger;
import org.ranG.genData.KeyFun;
import org.ranG.genData.LoggerUtil;
import org.ranG.grammar.SqlGenerator.SQLIterator;
import org.ranG.grammar.SqlGenerator.SQLRandomlyIterator;
import org.ranG.grammar.YaccParser.*;

import java.util.ArrayList;
import java.util.HashMap;


public class Grammar {
    static Logger log = LoggerUtil.getLogger();
    final int tknInit = 0;
    final int inSingQuoteStr = 1;
    final int inDoubleQuoteStr = 2;
    final int inOneLineComment = 3;
    final int inComment = 4;
    final int inCodeBlock = 5;
    final int inCodeBlockStr = 6;
    final int inCodeBlockSingleLineComment = 7;
    final int prepareCodeBlockMultiLineComment = 8;
    final int inCodeBlockMultiLineComment = 9;
    final int endCodeBlockMultiLineComment = 10;
    final int inKeyWord = 11;
    final int inNonTerminal = 12;
    final int inTerminal = 13;
    public static HashMap<Character,Integer> stateMap = new HashMap<>(){{
        stateMap.put('\'',1);
        stateMap.put('"',2);
        stateMap.put('#',3);
        stateMap.put('{',5);
        stateMap.put('_',11);
    }};
    public static HashMap<Character,Boolean> specialRune = new HashMap<>(){{
        specialRune.put(',',true);
        specialRune.put(';',true);
        specialRune.put('(',true);
        specialRune.put(')',true);
    }};


    public static boolean tknEnd(RuneSeq reader,char r){
        return Character.isSpaceChar(r) || r == '|' || specialRune.containsKey(r) || r == '#' || r == '{' || (r ==':' && !reader.peekEqual('=')) || (r =='/' && reader.peekEqual('*'));
    }

    int runeInitState(char c){
        if(stateMap.containsKey(c) ){
            return stateMap.get(c);
        }
        if(Character.isLowerCase(c)){
            return inNonTerminal;
        }
        return inTerminal;
    }
    public SQLIterator newIterWithRander(String yy, String root, int maxRecursive, KeyFun keyf){
        /* parse yy file first */
        ParseRet parseRet = parse(yy);
        if(parseRet == null){
            log.error("grammar : newIterWithRander fail to get parse result");
            return null;
        }
        SQLRandomlyIterator sqlRandomlyIterator = new SQLRandomlyIterator();
        SQLIterator sqlIter = sqlRandomlyIterator.generateSQL(parseRet.cbs,parseRet.mp,keyf,root,maxRecursive);
        if(sqlIter ==  null){
            log.error("newIterWithRander:fail to get iter");
            return null;
        }
        return sqlIter;

    }


    public HashMap<String, Production> initProductionMap(ArrayList<Production> productions){
        HashMap<String,Production> productionMap = new HashMap<>();
        for(Production production: productions){
            if(productionMap.containsKey(production.head.originString())){
                Production pm = productionMap.get(production.head.originString());
                pm.alter.addAll(production.alter);
                continue;
            }
            productionMap.put(production.head.originString(),production);
        }
        return  productionMap;
    }
    /* many return type*/
    public ParseRet parse(String yy){
        RuneSeq reader = new RuneSeq(yy.toCharArray(),0);
        Parser parser = new Parser();
        Tokener tkner = new Tokener(reader);
        ParseRet ret = parser.parseInside(tkner);  /* 内部使用tokener实现的的func接口 */
        if(ret == null){
            log.error("Grammar : parseInside error");
            return null;
        }
        ret.mp = initProductionMap(ret.pds);
        return ret;
    }


}
