package org.ranG.genData;

import java.util.ArrayList;
import java.util.HashMap;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Str;
import org.ranG.ComposeGen;
import org.ranG.genData.generators.*;
import org.ranG.genData.LuaParser;

public class Data {
    static final String numberType = "numbers";
    static final String blobType ="blobs";
    static final String temporalType = "temporas";
    static final String enumType = "enum";
    static final String stringType = "strings";

    HashMap<String,Generator> gens;

    String[] dataType = {"numbers","strings"};
    static HashMap<String,String> summaryType = new HashMap<>(){{
        put("int",numberType);
        put("bigint",numberType);
        put("float",numberType);
        put("double",numberType);
        put("decimal",numberType);
        put("numeric",numberType);
        put("fixed",numberType);
        put("bool",numberType);
        put("bit",numberType);

        put("blob",blobType);
        put("text",blobType);
        put("binary",blobType);

        put("data",temporalType);
        put("time",temporalType);
        put("year",temporalType);

        put("enum",enumType);
        put("set",enumType);
    }};
    static VarWithDefault[] defaultData = {
            new VarWithDefault(numberType,new String[]{
                    "digit", "digit", "digit", "digit", "null"
            }),
            new VarWithDefault(stringType,new String[]{
                    "letter", "letter", "letter", "letter", "null"
            }),
            new VarWithDefault(temporalType,new String[]{
                    "date", "time", "datetime", "year", "timestamp", "null"
            }),
            new VarWithDefault(enumType,new String[]{
                    "letter", "letter", "letter", "letter", "null"
            })
    };
    class constGen implements Generator{
        String constant;

        @Override
        public String gen() {
            return this.constant;
        }
    }

    Data(String key,LuaValue l){
        LuaParser parser = new LuaParser();
        HashMap<String, ArrayList<String>> datas = parser.extractAllSlice("data",dataType);
        HashMap<String,Generator> gens = new HashMap<>();
        for(String keys: datas.keySet()){
            ArrayList<String> values = datas.get(keys);

            gens.put(keys,)

        }
    }

    public Generator composeFromGenName(ArrayList<String>  genName){
        ArrayList<Generator> gs = new ArrayList<>();
        Register register = new Register();
        for(String gName : genName){
            Generator gor = register.get(gName);
            /* todo 这里忽略了 类型不存在的情况 */
            gs.add(gor);
        }
        ComposeGen cpg = new ComposeGen(gs);

    }
}
