package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

import static org.ranG.Main.zzPath;

public class DdlGenerator {
    String dsn;
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
        /* 能读到数据，并且除了lua脚本的，还有默认的数据 */
        Data data = new Data("",code);

        ZzConfig zzConfig = new ZzConfig(tb,fd,data);  /*这个是对构造 */

        ConfigRet genRet = zzConfig.byConfig();

        return  genRet;

    }
    public void act(){
        /*包含生成ddl语句(只是第一步） ， 连接DB ， 执行 ddl语句 */
        /* return : sql string[] ,keyFunc */
        Logger log = LoggerUtil.getLogger();
        ConfigRet dlls = getDdl();
        try{
            Connection conn = DriverManager.getConnection(this.dsn, "root", "jk123j@!?2<d");
            log.info("connect to sql success");
            Statement stmt = conn.createStatement();
            for(String sql:dlls.ddls){
                stmt.executeUpdate(sql);
            }

            /* for query */
//            ResultSet rs = stmt.executeQuery("SELECT * from table_90_utf8_undef");
//            while(rs.next()){
//                System.out.println("the varchar "+rs.getArray("col_enum_undef_signed"));
//            }

        }catch (SQLException e) {
            log.error("sql exception");
            throw new RuntimeException(e);
        }

    }

}
