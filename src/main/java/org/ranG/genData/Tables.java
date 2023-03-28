package org.ranG.genData;


import org.luaj.vm2.LuaValue;
import org.ranG.genData.generators.Generator;
import org.stringtemplate.v4.ST;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tables  {
    int number;
    ArrayList<String> fields;
    ArrayList<Integer> pos;
    HashMap< String,ArrayList<String> > datas;
    String tableTmpl;  /* string 的模板 */
    ST tmpl;

    /*
        property underneath is just for handler
     */
    static String tNamePrefix = "table";
    ByteBuffer buf;
    HashMap<String,String> m;
    ArrayList<Tables.TableStmt> stmts;

    public void setGenProperty(){
        buf = ByteBuffer.allocate(2048);
        m = new HashMap<>();
        stmts = new ArrayList<>();
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

    /* 无参数构造 */
    public Tables(){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
        tmpl = new ST("create table <tname> (\n" + "`pk` int primary keys <keys>\n" + ") <charsets> <partition>" );
    }
    public Tables(String template, String option, LuaValue lValue){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
        tmpl = new ST("create table <tname> (\n" + "`pk` int primary keys <keys>\n" + ") <charsets> <partition>" );

        /* tableVar : name String, default String[] */
        for(VarWithDefault var : tableVars){
            LuaParser parser = new LuaParser();
            ArrayList<String> vals = parser.extractSlice(lValue,option,var.name,var.defaultValue);
            this.addFields(var.name,vals);
        }

        /* 进行 template 的初始化 */


    }

    public ArrayList<TableStmt> gen(){
        setGenProperty();
//        TableHandler hd = new TableHandler();
        if(traverseEntry() < 0){
            return null;
        }else{
            return this.stmts;
        }
    }

    public String format(HashMap<String,String> vals){
        /* map 中所有kv 进行一个替换 */
        Iterator<Map.Entry<String,String>> it = vals.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            tmpl.add(entry.getKey(),entry.getValue());
        }
        return tmpl.render();

    }

    /* return value > 0 means ok | return vale < 0, error occurs  */
    /* 需要对传入父类的stmts array 进行修改*/
    int traverseEntry(){
        ArrayList<String> container  = new ArrayList<>();
        /* 子类需要父类的这个值 */
        TableHandler hd = new TableHandler(this.fields);
        /* 使用一个子类实现handler方法，传入，这个子类需要继承，这样才能用到父类的数据*/
        return traverse(container,0,hd);
    }
    int traverse(ArrayList<String> container,int idx,Handler hd){
        if(idx == this.fields.size()){
            return hd.anonFunc(container,stmts);
        }
        ArrayList<String> data = this.datas.get(this.fields.get(idx));
        for(String d:data){
            container.set(idx,d);
            if(traverse(container,idx+1,hd) < 0){
                return -1;
            }
        }
        /* normal case ,return 1*/
        return 1;
    }

}
