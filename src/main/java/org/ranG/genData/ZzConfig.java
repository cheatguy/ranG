package org.ranG.genData;

import org.apache.logging.log4j.Logger;

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
        genDdlReturn tableStmts = genDdls();
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
            st.wrapInTable(fieldRet.arr1);
        }
        genDdlReturn ret = new genDdlReturn();
        ret.arrTb = tableStmts;
        ret.arrFld = fieldRet.arr2;

        return ret;


        /* 对进行实例化 */


    }
}
