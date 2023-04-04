package org.ranG.grammar;

import org.ranG.genData.KeyFun;
import org.ranG.grammar.SqlGenerator.SQLRandomlyIterator;
import org.ranG.grammar.YaccParser.ParseRet;
import org.ranG.grammar.YaccParser.RuneSeq;
import org.ranG.grammar.YaccParser.Parser;

import java.util.HashMap;

public class Grammar {
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


    public boolean tknEnd(RuneSeq reader,char r){
        return Character.isSpaceChar(r) || r == '|' || specialRune.containsKey(r) || r == '#' || r == '{' || (r ==':' && !reader.peakEqual('=')) || (r =='/' && reader.peakEqual('*'));
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
    public SQLRandomlyIterator newIterWithRander(String yy, String root, int maxRecursive, KeyFun keyf,boolean debug){
        parse(yy);

    }

    /* many return type*/
    public ParseRet parse(String yy){
        RuneSeq reader = new RuneSeq(yy.toCharArray(),0);
        Parser parser = new Parser();
        ParseRet ret = parser.parseInside(IToken);

    }

}
