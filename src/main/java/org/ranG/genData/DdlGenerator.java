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
    SQLType t;
    enum SQLType {
        MYSQL,
        MARIADB,
        ALL
    }
    public void setType(String dbName){
        if(dbName.equals("MYSQL")){
            t = SQLType.MYSQL;
        }else if(dbName.equals("ALL")){
            t = SQLType.ALL;
        }
    }



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
        if(this.t == SQLType.MYSQL){
            try{
                Connection conn = DriverManager.getConnection(this.dsn, "root", "jk123j@!?2<d");
                log.info("connect to sql success");
                Statement stmt = conn.createStatement();
                for(String sql:dlls.ddls){
//                    stmt.executeUpdate(sql);
                    System.out.println(sql);
                }

            }catch (SQLSyntaxErrorException e){
                log.info("[fuzz test] a SQL is not syntax correct  ");
            } catch (SQLException e) {

                throw new RuntimeException(e);
            }

        }else if (this.t == SQLType.MARIADB){
            /* MariaDB */
            System.out.println("this is mariaDB");
            try{

                Connection conn = DriverManager.getConnection(this.dsn, "root", "nkl213HJKS&#HG*");
                Statement stmt = conn.createStatement();
                for(String sql:dlls.ddls){
                    stmt.executeUpdate(sql);
                }
            }catch (SQLSyntaxErrorException e){
                log.info("[fuzz test] a SQL is not syntax correct  ");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else if(this.t ==SQLType.ALL){
            try{
                Connection connMaria = DriverManager.getConnection("jdbc:mariadb://localhost:3308/cpy", "root", "nkl213HJKS&#HG*");
                Connection connMySQL = DriverManager.getConnection("jdbc:mysql://localhost:3306/cpy", "root", "jk123j@!?2<d");
                Statement stmtMarai = connMaria.createStatement();
                Statement stmtMySQL = connMySQL.createStatement();
                for(String sql:dlls.ddls){
                    stmtMarai.executeUpdate(sql);
                    stmtMySQL.executeUpdate(sql);
                }
            }catch(SQLException e){
                e.printStackTrace();
            }

        }

    }

}
