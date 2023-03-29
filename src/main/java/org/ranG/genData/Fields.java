package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;
import org.ranG.genData.generators.Generator;
import org.stringtemplate.v4.ST;

import java.awt.event.FocusEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.ranG.genData.Tables.tableVars;

public class Fields  {
    /* field ，table ，都是option 的这个结构 */
    int number;
    ArrayList<String> fields;
    ArrayList<Integer> pos;
    HashMap<String,ArrayList<String>> datas;
    ST tmpl;

    static Map<String, Boolean> canUnSign = new HashMap<>();
    static {
        canUnSign.put("tinyint",true);
        canUnSign.put("smallint",true);
        canUnSign.put("mediumint",true);
        canUnSign.put("int",true);
        canUnSign.put("bigint",true);
        canUnSign.put("float",true);
        canUnSign.put("double",true);
        canUnSign.put("decimal",true);

    }

    class FieldExec{
        boolean canUnSign;
        boolean unSign;
        String name;
        /* tp written by zz file*/
        String tp;
    }

    static String fNamePrefix ="col";
    HashMap<String,String> m;
    ArrayList<String> stmts;
    ArrayList<String> extraStmts;
    ArrayList<FieldExec>  fieldExecs;

    public void setProperty(){
        m = new HashMap<>();
        stmts = new ArrayList<>();
        extraStmts = new ArrayList<>();
        fieldExecs = new ArrayList<>();
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
    Fields( String option, LuaValue lValue){
        datas = new HashMap<>();
        fields = new ArrayList<>();
        pos = new ArrayList<>();
        tmpl = new ST("`<fname>` <types> <sign> <keys>");
        /* tableVar : name String, default String[] */
        for(VarWithDefault var : fieldVars){
            LuaParser parser = new LuaParser();
            ArrayList<String> vals = parser.extractSlice(lValue,option,var.name,var.defaultValue);
            this.addFields(var.name,vals);
        }
    }


    public FieldRet gen() {
        Logger log = LoggerUtil.getLogger();
        setProperty();
        if(traverseEntry() <0 ){ /*会对stmts 进行修改*/
            log.error("field gen() error");
            FieldRet ret = new FieldRet(this.stmts,this.fieldExecs);
            ret.arr1 = null;/*第一个返回空 */
            return ret;
        }else{

            this.stmts.addAll(extraStmts);
            FieldRet ret = new FieldRet(this.stmts,this.fieldExecs);
            return ret;
        }
    }

    void addFields(String fieldName, ArrayList<String> optionData) {
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
    public String format(HashMap<String,String> vals){
        Iterator<Map.Entry<String,String>> it = vals.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, String> entry = it.next();
            tmpl.add(entry.getKey(),entry.getValue());
        }
        return tmpl.render();
    }
    int traverseEntry(){
        ArrayList<String> container  = new ArrayList<>();
        /* 子类需要父类的这个值 */
        return traverse(container,0);
    }
    int traverse(ArrayList<String> container,int idx){
        if(idx == this.fields.size()){
            return passInto(container);
        }
        ArrayList<String> data = this.datas.get(this.fields.get(idx));
        for(String d:data){
            container.add(idx,d);
            if(traverse(container,idx+1) < 0){
                return -1;
            }
        }
        return 1;
    }
    /* 这个函数就是传入travers（） 中的那个函数 */
    int passInto(ArrayList<String> cur){
        Logger log = LoggerUtil.getLogger();
        FieldExec fExec = new FieldExec();
        String fName = this.fNamePrefix + String.join("_",cur);
        int extraNum = 0;
        for(int i=0;i<cur.size();i++){
            String field = this.fields.get(i);
            if(field.equals("types")){
                fExec.tp = cur.get(i).toLowerCase();
            }
            /*根据field 内容的不同，执行不同的函数
            * input : cur.get(i)
            *       : fName
            *       : fExec
            *
            * */
            FieldFuncRet fieldFuncRet = new FieldFuncRet();
            switch (field) {
                case "types":{
                    int idx = cur.get(i).indexOf('(');
                    String tp= "";
                    if(idx == -1){
                        tp = cur.get(i);
                    }else{
                        /* 获取 （前的内容 */
                        tp = cur.get(i).substring(0,idx-1);
                    }
                    tp = tp.toLowerCase();
                    if(canUnSign.containsKey(tp)){
                        fExec.canUnSign = true;
                    }
                    String retForType = cur.get(i);
                    if(tp.equals("set") || tp.equals("enum")){
                        /* todo：这里替换了go代码，不知道会不会出错 */
                        retForType = tp + "('a','b','c','d','e','f','g','h','i','j','k','l'," +
                        "'m','n','o','p','q','r','s','t','u','v','w','x','y','z')";
                    }
                    fieldFuncRet.target = retForType;
                    fieldFuncRet.ignore = false;
                    fieldFuncRet.extraStmt = null;
                    break;

                }
                case "keys" :{
                    if(cur.get(i).equals("undef")){
                        fieldFuncRet.target = "";
                        fieldFuncRet.ignore = false;
                        fieldFuncRet.extraStmt = null;
                        break;
                    }
                    String extra = String.format("key (`%s`)",fName);
                    fieldFuncRet.target = "";
                    fieldFuncRet.ignore = false;
                    fieldFuncRet.extraStmt = extra;
                    break;
                }
                /* "signed" is sign, other is "unsigned" */
                case "sign" :{
                    if(fExec.canUnSign){
                        if(cur.get(i).equals("signed")){
                            fieldFuncRet.target = "";
                            fieldFuncRet.ignore = false;
                            fieldFuncRet.extraStmt = null;
                            break;
                        }
                        fExec.unSign = true;
                        fieldFuncRet.target = "unsigned";
                        fieldFuncRet.ignore = false;
                        fieldFuncRet.extraStmt = null;
                        break;
                    }else if(!cur.get(i).equals("signed")){
                        fieldFuncRet.target = "";
                        fieldFuncRet.ignore = true;
                        fieldFuncRet.extraStmt = null;
                        break;
                    }
                    fieldFuncRet.target = "";
                    fieldFuncRet.ignore = false;
                    fieldFuncRet.extraStmt = null;
                    break;
                }
                default:{
                    log.error("field type don't match");
                }
            }
            //
            if(fieldFuncRet.ignore){
                this.extraStmts = new ArrayList<>(this.extraStmts.subList(0,extraStmts.size()-extraNum));
                return 1;
            }
            /*针对 “key”并且undef 的情况 */
            if (fieldFuncRet.extraStmt != null){
                extraNum++;//inner var
                extraStmts.add(fieldFuncRet.extraStmt);
            }
            this.m.put(field,fieldFuncRet.target);
        }
        this.m.put("fname",fName);
        fExec.name = fName;
        this.fieldExecs.add(fExec);
        this.stmts.add(format(this.m));
        return 1;
    }
}
