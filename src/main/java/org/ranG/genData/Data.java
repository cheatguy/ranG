package org.ranG.genData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;
import org.ranG.genData.generators.*;

public class Data {
    static final String numberType = "numbers";
    static final String blobType ="blobs";
    static final String temporalType = "temporals";
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


    Data(String key,LuaValue l){  /* parameter now is unused */
        LuaParser parser = new LuaParser();
        HashMap<String, ArrayList<String>> datas = parser.extractAllSlice("data",dataType);
        /* 能读到Lua 中的脚本信息 */
        HashMap<String,Generator> gens = new HashMap<>();
        for(String keys: datas.keySet()){
            ArrayList<String> values = datas.get(keys);
            gens.put(keys,composeFromGenName(values));
        }
        /*分两次向 gens map中添加 k-v */
        for(VarWithDefault val :defaultData){
            /* if not exist , add the default data */
            if(!gens.containsKey(val.name)){
                Generator dVal = composeFromGenName(new ArrayList<>(Arrays.asList(val.defaultValue)));
                gens.put(val.name,dVal);
            }
        }
        this.gens = gens;
    }

    public Generator composeFromGenName(ArrayList<String>  genName){
        ArrayList<Generator> gs = new ArrayList<>();
        Register register = new Register();
        for(String gName : genName){
            /* 内部进行封装好了，如果不存在，使用constGen（“name”） */
            Generator gor = register.get(gName);
            gs.add(gor);
        }
        ComposeGen cpg = new ComposeGen(gs);
        return cpg;
    }
    public ComposeGen getRecordGen(ArrayList<Fields.FieldExec> field){
        /* 这部分需要重新描述 */
        Logger log = LoggerUtil.getLogger();
        ArrayList<Generator> gensTmp = new ArrayList<>();
        for(Fields.FieldExec f:field){
            String name = f.tp;
            /*判断内容是否存在 */
            /* full type name */
            if(this.gens.containsKey(name)){
                /* 在data 中是存在这个映射的  */
                gensTmp.add(this.gens.get(name));
                continue;
            }
            /*simple type name */
            int idx = name.indexOf('(');
            if(idx != -1){
                /* get the string before ( */
                name = name.substring(0,idx);
                if(this.gens.containsKey(name)){
                    gensTmp.add(this.gens.get(name));
                    continue;
                }
            }
            /*fully summary name */
            String summaryKey = "";
            if(!summaryType.containsKey(name)){
                summaryKey = "strings";
            }else{
                summaryKey = summaryType.get(name);
            }
            if (!this.gens.containsKey(summaryKey)) {
                log.error("get recordGen : not find the corresponding type");
            }
            Generator generator = this.gens.get(summaryKey);
            if(f.unSign){
                gensTmp.add(new UnSignGen(generator,10,"1"));
            }else{
                gensTmp.add(generator);
            }
        }
        return new ComposeGen(gensTmp);
    }

}
