package org.ranG.genData;

import org.apache.logging.log4j.Logger;
import org.ranG.grammar.Grammar;
import org.ranG.grammar.SqlGenerator.SQLIterator;
import org.ranG.grammar.SqlGenerator.SQLVisitor;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.ranG.Main.*;
import static org.ranG.Main.yyPath;

public class DiffTestor {
    String dsnMySQL;
    String dsnMariaDB;
    int testNum;
    static Connection connMySQL,connMaria;
    static Logger log = LoggerUtil.getLogger();
    static String yyPath ="D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\examples\\diff.yy";
    public DiffTestor(String dsnMySQL,String dsnMariaDB,int testNum){
        this.dsnMariaDB = dsnMariaDB;
        this.dsnMySQL = dsnMySQL;
        this.testNum = testNum;
    }
    public boolean connectDB() {

        try{
            connMaria = DriverManager.getConnection(this.dsnMariaDB, "root", "nkl213HJKS&#HG*");

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        try{
            connMySQL = DriverManager.getConnection(this.dsnMySQL, "root", "jk123j@!?2<d");

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public KeyFun byDb() {
        Logger log = LoggerUtil.getLogger();
        try{
            DatabaseMetaData metaData = connMySQL.getMetaData();
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
            for(int i=0;i<tableStmts.size();i++){
                rs = metaData.getColumns(null, null, tableStmts.get(i).name, null);
                int cnt = 0;
                while(rs.next()) {
                    cnt++;
                }
                if(cnt >3){

                    rs = metaData.getColumns(null, null, tableStmts.get(i).name, null);
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
                    break;
                }

            }

            return new KeyFun(tableStmts,fieldExecs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadYy(){
        Logger log = LoggerUtil.getLogger();
        log.info("loadYy : the yy file is "+this.yyPath);
        try {
            String content = Files.readString(Paths.get(this.yyPath));
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

    SQLVisitor fixedTimesVisitor(IFixedTimesVisitor ifunc, int queryNum){
        final int[] cnt = {0};
        /*希望在匿名内中修改cnt的值 */

        return  new SQLVisitor() {

            @Override
            public boolean func(String sql) {
                ifunc.func(cnt[0],sql);
                cnt[0]++;
                if(cnt[0] == testNum){
                    return false;
                }
                return true;
            }
        };
    }
    public ArrayList<String> getRandSqls(KeyFun keyf){
        ArrayList<String> randomSqls= new ArrayList<>();

        /* pos——neg 解析不出来*/
        SQLIterator sqlIter = getIter(keyf);


        sqlIter.visit(fixedTimesVisitor( new IFixedTimesVisitor() {
            @Override
            public void func(int i, String sql) {
                randomSqls.add(sql);
            }
        },queryNum));
        return randomSqls;

    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e1) {
            try {
                Float.parseFloat(str);
                return true;
            } catch (NumberFormatException e2) {
                return false;
            }
        }
    }

    void compare(ArrayList<String> randSql){

        int totalNum = 0; //一共的查询个数
        int validNum = 0; //有效的查询
        int equalNum = 0; //相等的查询
        for(String sql:randSql){
            try{
                boolean currentSQLOK = true;
                totalNum++;
                Statement stmt_1 = connMySQL.createStatement();
                Statement stmt_2 = connMaria.createStatement();
                ResultSet set_1 = stmt_1.executeQuery(sql);
                ResultSet set_2 = stmt_2.executeQuery(sql);
                /* 有效执行条数 */

                validNum++;
                /* compare the ret size first*/
                if(set_1.getFetchSize() != set_2.getFetchSize()){
                    log.error("size not match ,the first is "+set_1.getFetchSize()+" and the second is "+set_2.getFetchSize());
                    return;
                }
                ResultSetMetaData rsmd_1 = set_1.getMetaData();
                int columnCount_1 = rsmd_1.getColumnCount();
                ResultSetMetaData rsmd_2 = set_2.getMetaData();
                int columnCount_2 = rsmd_2.getColumnCount();


                /* 可能是无序的，对输出结果进行排序 */
                ArrayList<String> list1 = new ArrayList<>(),list2 = new ArrayList<>();
                while(set_1.next() && set_2.next()){ //两者都存在相同的数据
                    /* 用于判断整体，跳出多重循环*/
                    for(int i =1;i<=columnCount_2 ;i++){
                        if(set_1.getString(i) != null){
                            list1.add(set_1.getString(i));
                        }
                        if(set_2.getString(i) != null){
                            list2.add(set_2.getString(i));
                        }
                    }
                }
                Collections.sort(list1);
                Collections.sort(list2);
                if(list2.size() !=list1.size()){
                    currentSQLOK = false;
                    continue;
                }
                for(int i=0;i<list2.size();i++){
                    if( isNumeric(list1.get(i)) && isNumeric(list2.get(i))){
                        /* 两者都为数字，不比较字符串，比较数值 */
                        try{
                            float t1 = Float.parseFloat(list1.get(i));
                            float t2 = Float.parseFloat(list2.get(i));
                            if(Math.abs(t1 - t2 )> 0.01){
                                currentSQLOK = false;
                                log.error("[diff testing] find the distinct ,the SQL is"+sql);
                                break;
                            }

                        }catch (NumberFormatException e){
                        }
                    }else if(!list2.get(i).equals(list1.get(i))){
                        currentSQLOK = false;
                        log.error("[diff testing] find the distinct ,the SQL is"+sql);
                        break;
                    }
                }
                if(currentSQLOK  ){
                    equalNum++;
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        log.info("[diff testing]  result ,the total SQL is "+totalNum+" the valid SQL is "+validNum+" the equal SQL is "+equalNum);
        System.out.println(totalNum+" "+validNum+" "+equalNum);

    }


    public void act(){
        connectDB();
        KeyFun keyF = byDb();
        ArrayList<String> randSqls = getRandSqls(keyF);
        compare(randSqls);
    }
}
