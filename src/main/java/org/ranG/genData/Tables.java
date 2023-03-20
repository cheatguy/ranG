package org.ranG.genData;

import java.util.ArrayList;
import java.util.HashMap;

public class Tables {
    int number;
    ArrayList<String> fields;
    ArrayList<Integer> pos;
    HashMap<String,String[]> datas;

    void addFields(String fileName,String[] optionData) {
        /* call only add once */
        if(this.datas.get(fileName) == null){
            this.datas.put(fileName,optionData);
            this.fields.add(fileName);
            /* pos is what ?*/
            this.pos.add(0);
            if(this.number != 0){
                number = number * optionData.length;
            }else{
                number = optionData.length;
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

    Tables(String template,String option,String zzStr){
        for(VarWithDefault var : tableVars){

        }
    }

}
