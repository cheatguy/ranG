package org.ranG.genData;

import org.luaj.vm2.LuaValue;
import org.ranG.genData.generators.Generator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Tables  {
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
    /* inner class */
    class TableStmt{
        /*creat statement without field part */
        String format;
        String name;
        int rowNum;
        /* generate by wrapInTable() */
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

    public ArrayList<TableStmt> gen(){

        String tNamePrefix = "table";
        ArrayList<TableStmt> stmts = new ArrayList<>();
        ByteBuffer buf = ByteBuffer.allocate(2048);
        ArrayList<HashMap<String,String>> m;
        TableHandler hd = new TableHandler();
        if(traverseEntry(hd) < 0){
            return null;
        }else{
            return stmts;
        }
    }

    class TableHandler implements Handler{
        @Override
        public int anonFunc(ArrayList<String> cur, String tname, Buffer buf, ArrayList<HashMap<String,String>> m, Tables.TableStmt stmts){
            buf.reset();

            return 1;
        }
    }
    /* return value > 0 means ok | return vale < 0, error occurs  */
    int traverseEntry(){
        ArrayList<String> container  = new ArrayList<>();
        return traverse(container,0,new TableHandler());
    }
    int traverse(ArrayList<String> container,int idx,Handler hd){
        if(idx == this.fields.size()){
            return hd.anonFunc(container);
        }
    }

}
