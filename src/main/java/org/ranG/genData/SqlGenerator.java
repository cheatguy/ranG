package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.ranG.grammar.Grammar;
import org.ranG.grammar.SqlGenerator.SQLIterator;
import org.ranG.grammar.SqlGenerator.SQLVisitor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;

import static org.ranG.Main.*;

public class SqlGenerator {
    String dsn;
    int queryNum = 100;
    static Logger log = LoggerUtil.getLogger();
    Map<String, Function<String, String>> functionMap = new HashMap<>();
    static Connection conn;
    ArrayList<String> randomSqls;
    public SqlGenerator(String dsn){
        this.dsn = dsn;
        this.randomSqls= new ArrayList<>();  /* 初始化一个存值的 arr */
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

    public SQLIterator getIter(KeyFun keyf){
        String yy = loadYy();
        /* todo : break point */
        Grammar grammar = new Grammar();
        SQLIterator iterator= grammar.newIterWithRander(yy,root,maxRecursive,keyf);
        if(iterator == null){
            log.error("getIter: fail to get iter");
        }
        return  iterator;


    }

    SQLVisitor fixedTimesVisitor(IFixedTimesVisitor ifunc,int queryNum){
        final int[] cnt = {0};
        /*希望在匿名内中修改cnt的值 */
        /* todo :这里这个计数器有问题*/
        return  new SQLVisitor() {

            @Override
            public boolean func(String sql) {
                ifunc.func(cnt[0],sql);
                cnt[0]++;
                if(cnt[0] == queryNum){
                    return false;
                }
                return true;
            }
        };
    }

    public ArrayList<String> getRandSqls(KeyFun keyf){
        this.randomSqls= new ArrayList<>();

        /* pos——neg 解析不出来*/
        SQLIterator sqlIter = getIter(keyf);

        /*todo 存在问题 */
        //这里相当于一个计数器，生成 times 个的sql语句
        //目前就生成一条
        sqlIter.visit(fixedTimesVisitor( new IFixedTimesVisitor() {
            @Override
            public void func(int i, String sql) {
                randomSqls.add(sql);
            }
        },queryNum));
        return this.randomSqls;
        /* 为什么生成的sql 只有一段，而不是我们句子中的两句
        * 猜想:是分隔符；导致了提前的结束 */


    }
    public KeyFun byDb(){
        Logger log = LoggerUtil.getLogger();
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            ArrayList<Tables.TableStmt> tableStmts = new ArrayList<>();
            ArrayList<Fields.FieldExec> fieldExecs = new ArrayList<>();
            while(rs.next()){
                String tableName = rs.getString("TABLE_NAME");
                Tables tb = new Tables();
                Tables.TableStmt tmp = tb.new TableStmt();
                tmp.name = tableName;
                /* skip the sys_config  table,( not  visible for user */
                if( tmp.name.equals("sys_config")){
                    continue;
                }

                tableStmts.add(tmp);
            }
            if(tableStmts.size() < 0){
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
//        System.out.println("gensql succeess");
//        System.out.println("the size of sql is "+ randSqls.size());
//        for(int i =0 ;i<10 ;i++){
//            System.out.println(randSqls.get(i));
//            System.out.println("------------------------\n\n");
//        }
        /* exec these sql */
        System.out.println("gensql : start exec-----------------");
        try{
            Statement statement = conn.createStatement();
            int cnt = 0;
            for (String sql:randSqls){
                System.out.println("---------------------");
                System.out.println(sql);
//                ResultSet resultSet =  statement.executeQuery(sql);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }


    }
}
