package org.ranG.genData;

import java.util.HashMap;

public class TableOptions {
    int number;
    String[] fields;
    int[] pos;
    HashMap<String,String[]> datas;

    void addFields(String fileName,String[] optionData) {
        /* call only add once */
        if(this.datas.get(fileName) == null){
            this.datas[fileName] = optionData;
        }

    }
}
