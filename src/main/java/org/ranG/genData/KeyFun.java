package org.ranG.genData;
import org.apache.logging.log4j.Logger;
import org.ranG.genData.generators.Generator;
import org.ranG.genData.generators.Register;

import java.util.*;
import java.util.function.Function;

public class KeyFun {
    public HashMap<String,IFunc> funcMap = new HashMap<>();
    ArrayList<Tables.TableStmt> tables;
    ArrayList<Fields.FieldExec> fields;
    ArrayList<Fields.FieldExec> fieldInt = new ArrayList<>();
    ArrayList<Fields.FieldExec> fieldChar= new ArrayList<>();
    static int fInt = 0;
    static int fChar = 1;
    static HashMap<String,Integer> fClass = new HashMap<>(){{
        put("char",fChar);
        put("varchar",fChar);
        put("binary",fChar);
        put("varbinary",fChar);
        put("integer",fInt);
        put("int",fInt);
        put("smallint",fInt);
        put("tinyint",fInt);
        put("mediumint",fInt);
        put("bigint",fInt);
    }
    };

    public String joinFields(ArrayList<Fields.FieldExec> fields){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<fields.size();i++){
            Fields.FieldExec tf = fields.get(i);
            if(i==0){
                sb.append("`");
            }else{
                sb.append("`,`");
            }
            sb.append(tf.name);
            if(i == fields.size()-1){
                sb.append("`");
            }
        }
        return sb.toString();
    }
    KeyFun(ArrayList<Tables.TableStmt> tables, ArrayList<Fields.FieldExec> fields){
        this.tables = tables;
        this.fields = fields;

        /* fClass aims to simplify the type */
        /* 没有兼顾 float类型 */
        for(Fields.FieldExec fe:fields){
            if(fClass.containsKey(fe.getType())){
                if(fClass.get(fe.getType()) == fInt){
                    fieldInt.add(fe);
                }else if(fClass.get(fe.getType()) == fChar){
                    fieldChar.add(fe);
                }
            }
        }
        TableFunc f1 = new TableFunc();
        FieldFunc f2 = new FieldFunc();
        FieldInvariantFunc f3 = new FieldInvariantFunc();
        FieldIntFunc f4 = new FieldIntFunc();
        FieldIntListFunc f5 = new FieldIntListFunc();
        FieldCharFunc f6 = new FieldCharFunc();
        FieldCharListFunc f7 = new FieldCharListFunc();
        FieldListFunc f8 = new FieldListFunc();

        this.funcMap.put("_table",f1);
        this.funcMap.put("_field",f2);
        this.funcMap.put("_field_invariant",f3);
        this.funcMap.put("_field_int",f4);
        this.funcMap.put("_field_int_list",f5);
        this.funcMap.put("_field_char",f6);
        this.funcMap.put("_field_char_list",f7);
        this.funcMap.put("_field_list",f8);



        /* traverse */
        //这部分其实是把gmap中的对象注册到funcMap中,使用到了匿名函数 */
        for (Map.Entry<String, Generator> entry : Register.gMap.entrySet()) {
            this.funcMap.put("_"+entry.getKey(), new IFunc() {
                @Override
                public String func() {
                   return entry.getValue().gen();
                }
            });
        }

    }
    public RetStrBool gen(String key){
        /* 返回什么是错误值 ？ */
        if(this.funcMap.containsKey(key)){
            String res = this.funcMap.get(key).func();
            if(res == null){
                return null;
            }else{
                RetStrBool ret  = new RetStrBool(res,true);
                return ret;
            }
        }
        return new RetStrBool("",false );
    }


    public class TableFunc implements IFunc{

        @Override
        public String func() {
            if(tables.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no table");
                return null;
            }else{
                Random rand = new Random();
                return tables.get(rand.nextInt(tables.size())).name;
            }
        }
    }
    public class FieldFunc implements IFunc{

        @Override
        public String func() {
            if (fields.size() ==0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no field");
                return null;
            }else{
                Random rand = new Random();
                return("`"+fields.get(rand.nextInt(fields.size())).name+"`");
            }
        }
    }
    public class FieldInvariantFunc implements IFunc{

        @Override
        public String func() {
            if (fields.size() ==0) {
                Logger log = LoggerUtil.getLogger();
                log.error("there is no field");
                return null;
            }else{
                Random rand = new Random();
                return("`"+fields.get(rand.nextInt(fields.size())).name+"`");
            }
        }
    }
    /* potential problem ,may get `pk` in the result */
    public class FieldIntFunc implements IFunc{

        @Override
        public String func() {
            if(fieldInt.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no int field");
                return null;
            }else{
                Random rand = new Random();
                return("`"+fieldInt.get(rand.nextInt(fieldInt.size())).name+"`");
            }
        }
    }
    public class FieldIntListFunc implements IFunc{

        @Override
        public String func() {
            if(fieldInt.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no int field");
                return null;
            }else {
                return joinFields(fieldInt);
            }

        }
    }
    public class FieldCharFunc implements IFunc{

        @Override
        public String func() {
            if(fieldChar.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no char field");
                return null;
            }else {
                Random rand = new Random();
                return("`"+fieldChar.get(rand.nextInt(fieldChar.size())).name+"`");
            }
        }
    }
    public class FieldCharListFunc implements IFunc{

        @Override
        public String func() {
            if(fieldChar.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no char field");
                return null;
            }else {
                return joinFields(fieldChar);
            }
        }
    }
    public class FieldListFunc implements IFunc{

        @Override
        public String func() {
            /* not classify the int or char */
            if(fields.size() == 0){
                Logger log = LoggerUtil.getLogger();
                log.error("there is no field");
                return null;
            }else {
                return joinFields(fields);
            }
        }
    }
}
