package com.feng.fresh.sampleword;

import com.feng.fresh.model.EventEnum;
import com.feng.fresh.test.TestTriggerScoreCalculate;
import com.feng.fresh.train.EventParser;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by feng on 2016/9/1.
 */
public class TriggerLarger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerLarger.class);

    public static Map<EventEnum, Set<String>> type2wordsMap = Maps.newHashMap();
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();
    public static Map<String, Double> scoreMap = Maps.newHashMap();
    public static Map<String, Double> scoreTempMap = Maps.newHashMap(); //临时变量
    public static Map<String, Double> scoreLargerMap = Maps.newHashMap();
    public static Set<String> verbAndNonSet = Sets.newHashSet();

    public static Map<String, List<String>>  sampleWordNum2WordsMap;
    public static Map<String, String> word2NumMap;

    static{
        sampleWordNum2WordsMap = SampWordParse.label2wordMap;
        word2NumMap = SampWordParse.word2labelMap;

        String path = TriggerLarger.class.getClassLoader().getResource("seq/test-seq.txt").getPath();
        File file = new File(path);
        TestTriggerScoreCalculate.parseTest(file);
        scoreMap = TestTriggerScoreCalculate.getSorceMap();
        word2typeMap = TestTriggerScoreCalculate.getWord2typeMap();
        verbAndNonSet = TestTriggerScoreCalculate.getVerbAndNonword();

        for(Map.Entry<String, Double> entity : scoreMap.entrySet()){
                String word = entity.getKey();
                EventEnum type = word2typeMap.get(word);


                //找到相应的触发词====同义词词林
                String num = word2NumMap.get(word);

                List<String> sampleList = sampleWordNum2WordsMap.get(num);

                if(sampleList != null) {
                    for (String str : sampleList) {
                        if(verbAndNonSet.contains(str)){
                            word2typeMap.put(str, type);
                            scoreTempMap.put(str, scoreMap.get(word));
                            scoreLargerMap.put(str, scoreMap.get(word));
                        }
                    }
                }
            //System.out.println("【原触发词信息】：触发词："+word+"， 触发词类型："+word2typeMap.get(word)+"，触发词得分："+scoreMap.get(word));
            //System.out.println("【扩展触词信息】："+scoreLargerMap.keySet().toString());
            //System.out.println();
            scoreLargerMap = Maps.newHashMap();
        }
        scoreMap.putAll(scoreTempMap);
        LOGGER.info("【扩展的触发词信息】,{}", scoreMap.toString());


    }

    public static Map<EventEnum, Set<String>> getType2wordsMap() {
        return type2wordsMap;
    }

    public static void setType2wordsMap(Map<EventEnum, Set<String>> type2wordsMap) {
        TriggerLarger.type2wordsMap = type2wordsMap;
    }

    public static Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }

    public static void setWord2typeMap(Map<String, EventEnum> word2typeMap) {
        TriggerLarger.word2typeMap = word2typeMap;
    }

    public static Map<String, Double> getScoreMap() {
        return scoreMap;
    }
}
