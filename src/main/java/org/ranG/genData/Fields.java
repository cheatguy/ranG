package org.ranG.genData;

import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;

import static org.ranG.genData.Tables.tableVars;

public class Fields {
    /* field ，table ，都是option 的这个结构 */
    int number;
    ArrayList<String> fields;
    ArrayList<Integer> pos;
    HashMap<String,ArrayList<String>> datas;

    class FieldExec{
        boolean canUnSign;
        boolean unSign;
        String name;
        /* tp written by zz file*/
        String tp;

    }

    //这里的 default var，不知道为什么有keys，但是zz文件中不包含keys
    static VarWithDefault[] fieldVars ={
            new VarWithDefault("types",new String[]{
                    "int", "varchar", "date", "time", "datetime"
            }),
            new VarWithDefault("keys",new String[]{
                    "undef","key"
            }),
            new VarWithDefault("sign",new String[]{
                    "signed"
            })
    };
    Fields(String template, String option, LuaValue lValue){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();

        /* tableVar : name String, default String[] */
        for(VarWithDefault var : fieldVars){
            LuaParser parser = new LuaParser();
            ArrayList<String> vals = parser.extractSlice(lValue,option,var.name,var.defaultValue);
            this.addFields(var.name,vals);
        }
    }

    void addFields(String fieldName,ArrayList<String> optionData) {
        /* call only add once */
        if(this.datas.get(fieldName) == null){
            this.datas.put(fieldName,optionData);
            this.fields.add(fieldName);
            /* pos is what ?*/
            this.pos.add(0);
            if(this.number != 0){
                number = number * optionData.size();
            }else{
                number = optionData.size();
            }
        }
    }
    void traverse(){

    }

}
