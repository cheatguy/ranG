package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;



public class TableHandler extends Tables implements Handler{

    ArrayList<String> fields;
    public TableHandler(ArrayList<String> fields){
        this.buf = ByteBuffer.allocate(2048);
        this.m = new HashMap<>();
        this.fields = fields;
    }
    @Override
    public int anonFunc(ArrayList<String> cur,ArrayList<TableStmt> stmtsIn) {
        Logger log = LoggerUtil.getLogger();
        this.buf.reset();
        byte[] str = Tables.tNamePrefix.getBytes();
        this.buf.put(str);
        /* 这个stmt 不同于stmts */
        TableStmt stmt = new TableStmt();
        for(int i=0;i< cur.size();i++){
            /*
                field name  : fields[s]
                field value : cur[s]
             */
            this.fields.get(i);
            String putTmp = "_"+ cur.get(i);
            this.buf.put(putTmp.getBytes());
            String field = this.fields.get(i);
            /* 根据 field 内容不同，执行不同代码块 */
            String input = cur.get(i);
            String target = "";
            boolean errorOccur = false;
            switch (field){
                case "rows":{
                    try {
                        int rows = Integer.parseInt(input);
                        stmt.rowNum = rows;
                    } catch ( NumberFormatException e){
                        errorOccur = true;
                        /* 这里日志打出来 */
                        System.out.println("error occurs");
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
                    }
                    try{
                        int num = Integer.parseInt(input);
                        target = String.format("\\npartition by hash(pk)\\npartitions %d",num);
                    }catch (NumberFormatException e){
                        /*打日志
                         */
                        errorOccur = true;
                        target = "";
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
            }else{
                this.m.put(field,target);
            }
        }
        String tName = this.buf.toString();
        stmt.name = tName;
        this.m.put("tname",tName);

        /* 根据table tmpl 进行 m这个map中的值替换 */
        stmt.format = this.format(this.m);
        stmtsIn.add(stmt); /*对传入的父类的 stmts 进行修改 */
        return 0;
    }
}
