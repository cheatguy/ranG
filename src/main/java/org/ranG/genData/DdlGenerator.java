package org.ranG.genData;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

import static org.ranG.Main.zzPath;

public class DdlGenerator {
    String dsn;
    /* double? ,not sure this global can be used in LuaParser */
    static Globals globals = JsePlatform.standardGlobals();
    public void setDsn(String dsn){
        this.dsn = dsn;
    }
    /*
        read lua zz file
        exec zz file

        genDdlReturn contains:
            String[] : sql
            keyfunc
     */

    public ConfigRet getDdl() {


        String zzStr ;
        if (zzPath.isEmpty()){
            /* use relative path later */
            zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua";
        }else{
            Path filePath = Path.of(zzPath);
        }
        LuaValue code = globals.loadfile(zzPath).call();
        Tables tb = new Tables("tables",code);
        Fields fd = new Fields("fields",code);
        /*  看下data能不能读取数据*/
        Data data = new Data("",code);

        ZzConfig zzConfig = new ZzConfig(tb,fd,data);  /*这个是对构造 */

        ConfigRet genRet = zzConfig.byConfig();

        return  genRet;

    }
    public void act(){
        /*包含生成ddl语句(只是第一步） ， 连接DB ， 执行 ddl语句 */
        /* return : sql string[] ,keyFunc */
        ConfigRet dlls = getDdl();

    }

}
