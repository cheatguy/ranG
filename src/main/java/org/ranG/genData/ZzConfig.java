package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.ranG.ComposeGen;

import java.util.ArrayList;

public class ZzConfig {
    Tables tables;
    Fields fields;
    Data data;
    private genDdlReturn ret;

    public ZzConfig(Tables t,Fields f,Data d){
        this.tables = t;
        this.fields = f;
        this.data   = d;

    }

    public ConfigRet byConfig(){
        /* 调用了genDdl（） 获取初始数据结构，在这一步对数据结构实例化为sql String */
        genDdlReturn tableStmts = genDdls();
        Logger log = LoggerUtil.getLogger();
        if(tableStmts == null){
            log.error("byConfig : get tableStmt error");
            return null;
        }
        ComposeGen recordGor = this.data.getRecordGen(tableStmts.arrFld);
        ArrayList<String> sql = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        for(Tables.TableStmt tableStmtTmp:tableStmts.arrTb){
            sql.add(tableStmtTmp.ddl);
            ArrayList<String> valuesStmt = new ArrayList<>();
            for(int i =0;i<tableStmtTmp.rowNum;i++){
                recordGor.oneRow(row);
                valuesStmt.add(WrapTool.wrapInDml(Integer.toString(i),row));
            }
            sql.add(WrapTool.wrapInInsert(tableStmtTmp.name,valuesStmt));
        }

        /* now ignore keyFunc */
        ConfigRet  ret = new ConfigRet(sql,null);
        return ret;


    }

    public genDdlReturn genDdls(){
        Logger log = LoggerUtil.getLogger();
        ArrayList<Tables.TableStmt> tableStmts = this.tables.gen();
        if(tableStmts == null){
            log.error("getDdl : table gen() return error");
            return null;

        }
        FieldRet fieldRet = this.fields.gen();
        if(fieldRet == null){
            log.error("getDdl : field get() return error");
            return null;
        }
        for(Tables.TableStmt st:tableStmts){
            /*
                只对tableStmts 中的每个 st元素中的 .ddl进行修改
             */
            st.wrapInTable(fieldRet.arr1);
        }
        genDdlReturn ret = new genDdlReturn();
        ret.arrTb = tableStmts;
        ret.arrFld = fieldRet.arr2;

        return ret;


        /* 对进行实例化 */


    }
}
