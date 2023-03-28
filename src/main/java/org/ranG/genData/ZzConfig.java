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
        genDdlReturn ret = new genDdlReturn();

        /*暂时fld 返回空，使用的是tablestmt */
        ret.arrFld = new ArrayList<>();
        ret.arrTb = ;

        /* 对进行实例化


    }
}
