package org.ranG.genData;

import org.ranG.genData.generators.Generator;

public class UnSignGen implements Generator {
    Generator generator;
    int retryNum;
    String defaultVal;
    public UnSignGen(Generator generator,int retryNum,String defaultVal){
        this.generator = generator;
        this.retryNum = retryNum;
        this.defaultVal = defaultVal;
    }
    @Override
    public String gen() {
        /* todo ：这里存在问题 */
        for (int i=0 ;i<this.retryNum ;i++){
            String cur = this.generator.gen();
            /* string maybe null not a “” */
            if(!cur.startsWith("-")){
                return cur;
            }
        }
        return this.defaultVal;
    }
}
