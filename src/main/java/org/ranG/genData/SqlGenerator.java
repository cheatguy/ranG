package org.ranG.genData;

import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;
public class SqlGenerator {
    String dsn;
    Map<String, Function<String, String>> functionMap = new HashMap<>();
    static Connection conn;
    public SqlGenerator(String dsn){
        this.dsn = dsn;
    }
    public boolean connectDB() {
        try{
            conn = DriverManager.getConnection(this.dsn, "root", "jk123j@!?2<d");
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void act(){
        Logger log = LoggerUtil.getLogger();
        /* connect to db */
        if(!connectDB()){
            log.error("connect to db error");
            return;
        }
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
            ArrayList<Tables.TableStmt> tableStmts = new ArrayList<>();
            while(rs.next()){
                String tableName = rs.getString("TABLE_NAME");
                Tables tb = new Tables();
                Tables.TableStmt tmp = tb.new TableStmt();
                tmp.name = tableName;
                tableStmts.add(tmp);
            }
            /* get the column info，just focus on first tb */
            rs = metaData.getColumns(null, null, tableStmts.get(0).name, null);
            while(rs.next()){
                String fieldName,fieldType;
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
