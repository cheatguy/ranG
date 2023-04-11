package org.ranG.grammar.YaccParser;

import org.apache.logging.log4j.Logger;
import org.ranG.genData.LoggerUtil;
import org.ranG.genData.generators.Char;

import java.util.Stack;

import static org.ranG.grammar.Grammar.tknEnd;
import static org.ranG.grammar.YaccParser.Parser.initState;

/*实现一个方法IToken */
public class Tokener implements IToken{

    static Logger log = LoggerUtil.getLogger();
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
        int lookBackPos = -1 ; /*will change when first state */
        int luaCommentDepth = -1;
        int endLuaCommentDepCounter = -1;
        int lastState = state;

        while(true){
            /* java中是不存在EOF的 */
                char c = reader.readRune();
                /*todo:这里有个终止条件,需要修改 */
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
                            return new Terminal(common,String.valueOf(c));
                        }
                        /* change state */
                        lookBackPos = reader.pos - 1;

                        switch (c){
                            case '\'': {
                                state = inSingQuoteStr;
                                break;
                            }
                            case '"' : {
                                state = inDoubleQuoteStr;
                                break;
                            }
                            case '#' : {
                                state = inOneLineComment;
                                break;
                            }
                            case '_' : {
                                state = inKeyWord;
                                break;
                            }
                            case '{' : {
                                state = inCodeBlock;
                                stack.push('{');
                                break;
                            }
                            default :{
                                if(Character.isLowerCase(c)){
                                    state = inNonTerminal;
                                }else{
                                    state = inTerminal;
                                }
                                break;
                            }
                        }

                        break;
                    }


                    // handle EOF first
                    case inNonTerminal: {
                        if(c == 0){
                            return new NonTerminal(common,reader.slice(lookBackPos));
                        }
                        /* skip white space */
                        if(tknEnd(reader,c)){
                            reader.unReadRune();
                            return new NonTerminal(common,reader.slice(lookBackPos));
                        }
                        /* non terminal could only composed by lower char,digit or _ */
                        if(!Character.isLowerCase(c) && !Character.isDigit(c) && c != '_' ){
                            state = inTerminal;
                        }

                        break;
                    }
                    case inTerminal : {
                        if(c == 0){
                            return new Terminal(common,reader.slice(lookBackPos));
                        }
                        /* skip white space */
                        if(tknEnd(reader,c)){
                            reader.unReadRune();
                            return new Terminal(common,reader.slice(lookBackPos));
                        }
                        if(lastState == tknInit && reader.lastEqual('/') && c =='*'){
                            state = inComment;
                        }

                        break;
                    }

                    case inKeyWord : {
                        if(c==0 ||tknEnd(reader,c)){
                            if (c != 0){
                                reader.unReadRune();
                            }
                            String keyWordLiteral = reader.slice(lookBackPos);
                            if(keyWordLiteral.equals("_")){
                                return new Terminal(common,keyWordLiteral);
                            }
                            return new KeyWord(common,keyWordLiteral);
                        }

                        break;
                    }

                    case inOneLineComment: {
                        if( c==0 || c =='\n'){
                            return new Comment(reader.slice(lookBackPos));
                        }

                        break;
                    }

                    case inComment : {
                        // look back
                        if(c==0  || c =='\n'){
                            state = inTerminal;
                            reader.setPos(lookBackPos + 1);
                            continue;
                        }
                        if(reader.lastEqual('*') && c == '/'){
                            return new Comment(reader.slice(lookBackPos));
                        }

                        break;
                    }

                    case inSingQuoteStr : {
                        if(c == 0 || c =='\n'){
                            state = inTerminal;
                            reader.setPos(lookBackPos + 1);
                            continue;
                        }

                        if (c == '\''){
                            return new Terminal(common,reader.slice(lookBackPos));
                        }

                        break;
                    }
                    case inDoubleQuoteStr : {
                        if(c == 0 || c =='\n'){
                            state = inTerminal;
                            reader.setPos(lookBackPos + 1);
                            continue;
                        }
                        if(c =='"'){
                            return new Terminal(common,reader.slice(lookBackPos));
                        }

                        break;
                    }
                    case inCodeBlock : {
                        if( c== 0){
                            state = inTerminal;
                            reader.setPos(lookBackPos + 1);
                            stack.clear();
                            continue;
                        }

                        if(  c== '{'){
                            stack.push(c);
                        }else if( c == '}'){
                            stack.pop();
                            if(stack.empty()){
                                return new CodeBlock(common,reader.slice(lookBackPos));
                            }
                        }else if( c == '\'' || c=='"'){
                            stack.push(c);
                            state = inCodeBlockStr;
                        }else if(c =='-' && reader.lastEqual('-')){
                            state = inCodeBlockSingleLineComment;
                        }

                        break;
                    }
                    case inCodeBlockStr : {
                        if (c == 0){
                            state = inTerminal;
                            reader.setPos(lookBackPos + 1);
                            stack.clear();
                            continue;
                        }
                        if(stack.peek() != null){
                            char p = stack.peek();
                            if(c ==p && !reader.lastEqual('\\')){
                                stack.pop();
                                state = inCodeBlock;
                            }
                        }else{
                            log.error("impossible code path");
                            return null;
                        }

                        break;
                    }
                    case inCodeBlockSingleLineComment : {
                        if( c == 0 || c =='\n'){
                            state = inCodeBlock;
                            continue;
                        }
                        if(lastState == inCodeBlock && c == '['){
                            luaCommentDepth = 0;
                            state = prepareCodeBlockMultiLineComment;
                        }

                        break;
                    }
                    case prepareCodeBlockMultiLineComment : {
                        if(c == 0){
                            state = inCodeBlockSingleLineComment;
                            continue;
                        }
                        if(c =='['){
                            state = inCodeBlockMultiLineComment;
                        }else if(c == '='){
                            luaCommentDepth ++;
                        }else{
                            state = inCodeBlockSingleLineComment;
                        }

                        break;
                    }
                    case inCodeBlockMultiLineComment : {
                        if( c == 0){
                            log.error("Tokenizer : inCodeBlockMultiLineComment ,error at eof");
                            return null;
                        }
                        if( c ==']'){
                            endLuaCommentDepCounter = 0;
                            state = endCodeBlockMultiLineComment;
                        }

                        break;
                    }
                    case endCodeBlockMultiLineComment : {
                        if( c == 0){
                            log.error("Tokenizer : endCodeBlockMultiLineComment ,error at eof");
                            return null;
                        }
                        if(c == '='){
                            endLuaCommentDepCounter++;
                        }else if(c == ']' && endLuaCommentDepCounter == luaCommentDepth){
                            state = inCodeBlock;
                        }else{
                            state = inCodeBlockMultiLineComment;
                        }

                        break;
                    }

                }
                lastState = stateCopy;
        }
    }
}
