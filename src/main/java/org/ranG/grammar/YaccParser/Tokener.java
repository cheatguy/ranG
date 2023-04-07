package org.ranG.grammar.YaccParser;

import java.util.Stack;

import static org.ranG.grammar.YaccParser.Parser.initState;

/*实现一个方法IToken */
public class Tokener implements IToken{

    RuneSeq reader;
    public Tokener(RuneSeq seq){
        this.reader = seq;
    }
    static final int tknInit = 0;
    static final int inSingQuoteStr = 1;
    static final int inDoubleQuoteStr = 2;
    static final int inOneLineComment = 3;
    static final int inComment = 4;
    static final int inCodeBlock = 5;
    static final int inCodeBlockStr = 6;
    static final int inCodeBlockSingleLineComment = 7;
    static final int prepareCodeBlockMultiLineComment = 8;
    static final int inCodeBlockMultiLineComment = 9;
    static final int endCodeBlockMultiLineComment = 10;
    static final int inKeyWord = 11;
    static final int inNonTerminal = 12;
    static final int inTerminal = 13;


    @Override
    public Token func() {
        Stack<Character> stack = new Stack<>();
        CommonAttr common = new CommonAttr();
        int state = tknInit;
        int lookBackPos;
        int luaCommentDepth;
        int endLuaCommentDepCounter;
        int lastState = state;

        while(true){
            /* java中是不存在EOF的 */
                char c = reader.readRune();
//                if(c == 0){ /* 返回到最后一个EOF */
//                    return null;
//                }
                int stateCopy = state;
                switch (state){
                    case tknInit : {
                        if(c == 0){
                            return new Eof();
                        }
                        /* skip space */
                        if(Character.isWhitespace(c)){
                            common.hasPreSpace = true;
                            continue;
                        }
                        /* delimeter */
                        if( (c ==':' && !reader.peekEqual('=')) || c =='|' ){
                            return new Operator(String.valueOf(c));
                        }
                        /* special char */
                        Parser ps = new Parser();
                        if(ps.isSpecialChar(c)){
                            return new Terminal(String.valueOf(c),common);
                        }
                        /* change state */

                        break;
                    }
                    case tknInit : {


                        break;
                    }
                    case tknInit : {


                        break;
                    }
                    case tknInit : {


                        break;
                    }
                }
        }
    }
}
