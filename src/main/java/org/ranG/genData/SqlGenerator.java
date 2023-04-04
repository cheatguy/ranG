package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.ranG.grammar.SqlGenerator.SQLVisitor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;

import static org.ranG.Main.yyPath;

public class SqlGenerator {
    String dsn;
    int queryNum = 100;
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
    public KeyFun byDb(){
        Logger log = LoggerUtil.getLogger();
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
            ArrayList<Tables.TableStmt> tableStmts = new ArrayList<>();
            ArrayList<Fields.FieldExec> fieldExecs = new ArrayList<>();
            while(rs.next()){
                String tableName = rs.getString("TABLE_NAME");
                Tables tb = new Tables();
                Tables.TableStmt tmp = tb.new TableStmt();
                tmp.name = tableName;
                tableStmts.add(tmp);
            }
            if(tableStmts.size() > 0){
                log.error("sqlGenerator act: tableStatement size < 0");
            }
            /* get the column info，just focus on first tb */
            rs = metaData.getColumns(null, null, tableStmts.get(0).name, null);
            while(rs.next()){
                String fieldName,fieldType;
                fieldName = rs.getString("COLUMN_NAME");
                fieldType = rs.getString("TYPE_NAME");
                Fields fd = new Fields();
                Fields.FieldExec tmp = fd.new FieldExec();
                tmp.name = fieldName;
                tmp.tp   = fieldType;
                fieldExecs.add(tmp);
            }
            return new KeyFun(tableStmts,fieldExecs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadYy(){
        Logger log = LoggerUtil.getLogger();
        log.info("loadYy : the yy file is "+yyPath);
        try {
            String content = Files.readString(Paths.get(yyPath));
            return content;
        } catch (IOException e) {
            log.error("loadYy : read data fail");
            e.printStackTrace();
        }
        /* not sure */
        return "";

    }

    public SQLVisitor getIter(){
        String yy = loadYy();


    }
    public ArrayList<String> getRandSqls(KeyFun keyf){
        ArrayList<String> randomSqls = new ArrayList<>();

    }

    public void act(){
        Logger log = LoggerUtil.getLogger();
        /* connect to db */
        if(!connectDB()){
            log.error("connect to db error");
            return;
        }
        KeyFun keyF = byDb();
        ArrayList<String> randSqls = new ArrayList<>();
        randSqls = getRandSqls(keyF);



    }
}
