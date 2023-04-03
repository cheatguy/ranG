package org.ranG.genData;

import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;
public class SqlGenerator {
    String dsn;
    Map<String, Function<String, String>> functionMap = new HashMap<>();

    public SqlGenerator(String dsn){
        this.dsn = dsn;
    }
    public boolean connectDB() {
        try{
            Connection conn = DriverManager.getConnection(this.dsn, "root", "jk123j@!?2<d");
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally {

        }
        return true;
    }
    public void act(){
        Logger log = LoggerUtil.getLogger();
        /* connect to db */
        if(connectDB()){
            log.error("connect to db error");
        }
    }
}
