package org.ranG;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.ranG.genData.*;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
//    public static String zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\default.zz.lua";
    public static String zzPath = "D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\type_1.zz.lua";
    public static  int queryNum = 2000;
//    public static String yyPath ="D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\examples\\select.yy";
    public static String yyPath ="D:\\WorkSpace\\DB\\ranG\\src\\main\\java\\org\\ranG\\resource\\examples\\diff.yy";
    public static boolean debug = false;
    public static String root = "query";  /* root bnf expression to generate sql */
    public static  int maxRecursive = 5;
    public static void main(String[] args) {

            Logger log = LoggerUtil.getLogger();

            try{

            Options options = new Options();

            Option genData = new Option("g1","genData",true,"generate table and data");

            Option genTable = new Option("g2","genSql",true,"generate sql statement based on table structure");

            Option fullTest = new Option("g3","fullTest",true,"gen table,insert data and select");

            Option diffTest = new Option("g4","diffTest",true,"differential testing ");

            options.addOption(genData);
            options.addOption(genTable);
            options.addOption(fullTest);
            options.addOption(diffTest);
            log.info("after the command line parsing");
            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(options, args);

            /* create table and insert the data */

            if (cl.hasOption("genData")){
                // -genData jdbc:mysql://localhost:3306/cpy
                // -genData jdbc:mariadb://localhost:3308/cpy
                String dsns = cl.getOptionValue("genData");
                DdlGenerator generator = new DdlGenerator();
                generator.setDsn(dsns);
                //MARIA MYSQL FULL
                generator.setType("MYSQL");
                generator.act();


            }else if (cl.hasOption("genSql")){
                // -genSql jdbc:mysql://localhost:3306/cpy
                // -genSql jdbc:mysql://localhost:3306/cpy
                String dsn = cl.getOptionValue("genSql");
                SqlGenerator generator = new SqlGenerator(dsn,2000);
                generator.act();
            }else if(cl.hasOption("fullTest")){
                // -fullTest jdbc:mysql://localhost:3306/cpy
                String dsn = cl.getOptionValue("fullTest");
                FullTestor generator = new FullTestor(dsn);
                generator.act();
                /* 完整的测试流程 */
            }else if(cl.hasOption("diffTest")){
                //-diffTest jdbc:mysql://localhost:3306/cpy;jdbc:mariadb://localhost:3308/cpy
                String dsnList = cl.getOptionValue("diffTest");
                String[] dns = dsnList.split(";");
                DiffTestor diffTester = new DiffTestor(dns[0],dns[1],500);
                diffTester.act();

            }else {
                System.out.println("not found");
            }
        } catch ( ParseException e){
            e.printStackTrace();
        }

    }
}

/* todo : 多数据库支持，GUI，差分测试 */