package org.ranG.genData;


import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;
import org.ranG.genData.generators.Generator;
import org.stringtemplate.v4.ST;

import java.awt.image.AreaAveragingScaleFilter;
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
    ST tmpl;  /*这个ST没有用，用到的是format里面每次用新的ST */

    /*
        property underneath is just for handler
     */
    static String tNamePrefix = "table";
    StringBuilder buf;
    HashMap<String,String> m;
    ArrayList<Tables.TableStmt> stmts;

    public void setGenProperty(){
        this.buf = new StringBuilder();
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
    public class TableStmt{
        /*creat statement without field part */
        String format;
        String name;
        int rowNum;
        /* generate by wrapInTable() */
        String ddl;

        /* 只对tableStmt 中的.dll进行修改 */
        public void  wrapInTable(ArrayList<String> fieldStmts){
            StringBuilder bufT = new StringBuilder();
            bufT.append(",\n");
            bufT.append(String.join(",\n",fieldStmts));
            this.ddl = String.format(this.format,bufT.toString());
        }
    }
    static VarWithDefault[] tableVars ={new VarWithDefault("rows",new String[]{"0", "1", "2", "10", "100"}),new VarWithDefault("charsets",new String[]{"undef"}),new VarWithDefault("partitions",new String[]{"undef"})};

    /* 无参数构造 */
    public Tables(){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
//        tmpl = new ST("create table <tname> (\n" + "`pk` int primary key <keys>%s\n" + ") <charsets> <partitions>" );
        tmpl = new ST("create table <tname> (\n" + "\"pk\" int primary key <keys>%s\n" + ") <charsets> <partitions>" );
    }
    public Tables(String option, LuaValue lValue){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
//        tmpl = new ST("create table <tname> (\n" + "`pk` int primary key <keys>%s\n" + ") <charsets> <partitions>" );
        tmpl = new ST("create table <tname> (\n" + "\"pk\" int primary key <keys>%s\n" + ") <charsets> <partitions>" );


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
        if(traverseEntry() < 0){
            return null;
        }else{
            /* stmts 被作为参数，已经被add 了很多次了*/
            return this.stmts;
        }
    }

    public String format(HashMap<String,String> vals){
        /* map 中所有kv 进行一个替换 */
        Iterator<Map.Entry<String,String>> it = vals.entrySet().iterator();
        /*这个才是真正起作用的地方 */
        ST tmp = new ST("create table <tname> (\n" + "`pk` int primary key <keys>%s\n" + ") <charsets> <partitions>");
//        ST tmp = new ST("create table <tname> (\n" + "\"pk\" int primary key <keys>%s\n" + ") <charsets> <partitions>");
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            tmp.add(entry.getKey(),entry.getValue());
        }
        return tmp.render();

    }

    /* return value > 0 means ok | return vale < 0, error occurs  */
    /* 需要对传入父类的stmts array 进行修改*/
    int traverseEntry(){
        ArrayList<String> container  = new ArrayList<>();
        /* 子类需要父类的这个值 */
        /* 使用一个子类实现handler方法，传入，这个子类需要继承，这样才能用到父类的数据*/
        return traverse(container,0);
    }
    int traverse(ArrayList<String> container,int idx){
        if(idx == this.fields.size()){
            /* 满的时候，container 会各自包含一个 field 的值 */
            return passInto(container);
        }
        /* 取data 中的值，给对于的structure */
        ArrayList<String> data = this.datas.get(this.fields.get(idx));
        for(String d:data){
            /* Error: 这里爆出数组越界，原因是java的arrayList中如果没有对象，就不会进行set值,应该使用add（） not set（） */
            /* todo：这里要用set那种方法，但是set会引入错误,对数组边界进行判断 */
            if(idx < container.size()){
                container.set(idx,d);
            }else{
                container.add(idx,d); /*这个add 有问题，如果当前位置存在，他就会把整体右移 */
            }

            if(traverse(container,idx+1) < 0){
                return -1;
            }
        }
        /* normal case ,return 1*/
        return 1;
    }
    int passInto(ArrayList<String> cur){
        Logger log = LoggerUtil.getLogger();
        this.buf.setLength(0); /* delete buf*/
        this.buf.append(tNamePrefix);
        /* 这个stmt 不同于stmts */
        TableStmt stmt = new TableStmt();
        for(int i=0;i< cur.size();i++){
            /*
                field name  : fields[s]
                field value : cur[s]
             */
            /* cur 会不断膨胀，但是 field 大小没变化 */
            String field = this.fields.get(i);
            String putTmp = "_"+ cur.get(i);
            this.buf.append(putTmp);
            /* 根据 field 内容不同，执行不同代码块 */
            String input = cur.get(i);
            String target = ""; /* return value for switch */
            boolean errorOccur = false;
            switch (field){
                case "rows":{
                    try {
                        int rows = Integer.parseInt(input);
                        stmt.rowNum = rows;
                    } catch ( NumberFormatException e){
                        errorOccur = true;
                        target = "";
                        /* 这里日志打出来 */
                        log.error("table handler:parse int error");
                        break;
                    }
                    target = "";
                    break;
                }
                case "charsets":{
                    if(input.equals("undef")){
                        target = "";
                    }else{
                        target = String.format("character set %s",input);
                    }
                    break;
                }
                case "partitions":{
                    if(input.equals("undef")){
                        target ="";
                        break;
                        /*
                            bug： 这里忘记break，导致parse 到 “under”
                         */
                    }
                    try{
                        int num = Integer.parseInt(input);
                        target = String.format("\npartition by hash(pk)\npartitions %d",num);
                    }catch (NumberFormatException e){
                        /*打日志
                         */
                        log.error("table handler : parse int error");
                        errorOccur = true;
                        target = "";
                        break;
                    }
                    break;
                }
                default : {
                    log.info("handler work successful");
                }
            }
            /* m中的map 已经映射出很多 kv 了*/
            if(errorOccur){
                /* error occurs ,dont set map */
                target = "";
                return -1;
                /* 整体退出 ，返回-1 */
            }else{
                this.m.put(field,target);
            }
        }
        String tName = this.buf.toString();
        stmt.name = tName;
        this.m.put("tname",tName);

        /* 根据table tmpl 进行 m这个map中的值替换 */
        stmt.format = this.format(this.m);
        this.stmts.add(stmt); /*对传入的父类的 stmts 进行修改 */
        return 1;
    }

}
