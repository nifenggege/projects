package com.feng.fresh.test;

import com.feng.fresh.model.EventEnum;
import com.feng.fresh.sampleword.TriggerLarger;
import com.feng.fresh.tools.StringUtils;
import com.feng.fresh.train.EventParser;
import com.feng.fresh.train.TriggerScorePre;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by feng on 2016/9/12.
 */
public class CreateResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateResult.class);

    //阈值
    private static final Double THREAD = 0.1;
    private static int count = 0;

    private static int correctCount = 0;
    private static int standCount = 0;
    private static int recogCount = 0;

    //触发词候选--》得分
    private static Map<String, Double> sorceMap = Maps.newHashMap();
    //词==》类型
    public static Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    private static List<List<String>> sentenceTriggerList = Lists.newArrayList();

    static{

        sorceMap = TriggerLarger.getScoreMap();
        word2typeMap = TriggerLarger.getWord2typeMap();

        String path = CreateResult.class.getClassLoader().getResource("train/test-arg.txt").getPath();
        File file = new File(path);
        EventParser.parseEvent(file);
        LOGGER.info("【正确答案】，{}",EventParser.getTriggerMap().toString());
        sentenceTriggerList = EventParser.getSentenceTriggerList();
    }

    /**
     * 先看分词的结果
     * @param file
     */
    public static void resultFactory(File file){
        if(null==file) return;

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.txt")));

            String line = null;
            while((line=bufferedReader.readLine()) != null) {
                line = line.replaceAll("/[a-zA-Z^(//s)]*", "");
                Iterable<String> list = Splitter.on(Pattern.compile(StringUtils.SEPORETOR)).trimResults().omitEmptyStrings().split(line);
                for (String str : list) {
                    List<String> triggerForSentence = Lists.newCopyOnWriteArrayList(sentenceTriggerList.get(count));
                    //List<String> triggerForSentence = sentenceTriggerList.get(count);
                    standCount+= triggerForSentence.size();
                    Iterable<String> tokens = Splitter.on(Pattern.compile("(\\s)+")).trimResults().omitEmptyStrings().split(str);
                    for (String token : tokens) {

                        if(sorceMap.containsKey(token) && sorceMap.get(token)>THREAD){
                            recogCount++;
                            String isTure = triggerForSentence.contains(token)?"Y":"N";
                            if("Y".equals(isTure)){
                                correctCount++;
                                triggerForSentence.remove(token);
                            }
                            bufferedWriter.write(str.replaceAll("(\\s)+", "")+"\t"+token+"\t"+word2typeMap.get(token)+"\t"+isTure+"\t"+sentenceTriggerList.get(count));
                            bufferedWriter.newLine();
                        }
                    }
                    count++;

                }
            }

            LOGGER.info("【正确识别个数】,{}", correctCount);
            LOGGER.info("【识别总个数】,{}", recogCount);
            LOGGER.info("【标准答案个数】,{}", standCount);
            Double rRate = (1.0)*correctCount/standCount;
            Double pRate = (1.0)*correctCount/recogCount;
            Double f = 2.0*(rRate*pRate)/(rRate+pRate);
            LOGGER.info("【准确率】,{}", pRate);
            LOGGER.info("【召回率】,{}", rRate);
            LOGGER.info("【F值】,{}", f);



        } catch (FileNotFoundException e) {
            LOGGER.error("文件不能发现", e );
        } catch (IOException e) {
            LOGGER.error("IO异常", e );
        }
        finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error("IO异常", e );
                }
            }

            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    LOGGER.error("IO异常", e );
                }
            }


        }
        return ;
    }


}
