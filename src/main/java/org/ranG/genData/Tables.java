package org.ranG.genData;

import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;

public class Tables {
    int number;
    ArrayList<String> fields;
    ArrayList<Integer> pos;
    HashMap< String,ArrayList<String> > datas;


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

    class TableStmt{
        String format;
        String name;
        int rowNum;
        String ddl;
    }
    static VarWithDefault[] tableVars ={new VarWithDefault("rows",new String[]{"0", "1", "2", "10", "100"}),new VarWithDefault("charsets",new String[]{"undef"}),new VarWithDefault("partitions",new String[]{"undef"})};

    Tables(String template, String option, LuaValue lValue){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
        /* tableVar : name String, default String[] */
        for(VarWithDefault var : tableVars){
            LuaParser parser = new LuaParser();
            ArrayList<String> vals = parser.extractSlice(lValue,option,var.name,var.defaultValue);
            this.addFields(var.name,vals);
        }

    }
    /* this should exist in lua_parser */

}
