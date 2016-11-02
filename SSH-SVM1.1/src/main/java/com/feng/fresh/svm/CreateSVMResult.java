package com.feng.fresh.svm;

import com.feng.fresh.dp.DPParse;
import com.feng.fresh.model.EventEnum;
import com.feng.fresh.sampleword.SampWordParse;
import com.feng.fresh.sampleword.TriggerLarger;
import com.feng.fresh.tools.StringUtils;
import com.feng.fresh.train.EventParser;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by feng on 2016/9/12.
 */
public class CreateSVMResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSVMResult.class);

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

        String path = CreateSVMResult.class.getClassLoader().getResource("train/test-arg.txt").getPath();
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
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("svm.txt")));

            String line = null;
            while((line=bufferedReader.readLine()) != null) { //磁性标注的句子
                String[] poses = line.trim().split("[。！？]/wp");

                line = line.replaceAll("/[a-zA-Z^(//s)]*", "");
                Iterable<String> list = Splitter.on(Pattern.compile(StringUtils.SEPORETOR)).trimResults().omitEmptyStrings().split(line);
                //分句
                int sentenceIndex = 0;
                for (String str : list) {
                    List<String> triggerForSentence = Lists.newCopyOnWriteArrayList(sentenceTriggerList.get(count));
                    String[] pos = poses[sentenceIndex].trim().split("(\\s)+");
                    List<String> posList = Lists.newArrayList(pos);
                    LOGGER.info("==pos==={}", posList);
                    posList.add(0, "null/null");
                    posList.add(0, "null/null");
                    posList.add(0, "null/null");
                    posList.add(posList.size(), "null/null");
                    posList.add(posList.size(), "null/null");
                    posList.add(posList.size(), "null/null");

                    //List<String> triggerForSentence = sentenceTriggerList.get(count);
                    String sen = str.replaceAll("(\\s)+","");
                    standCount+= triggerForSentence.size();
                    String[] tokens = str.split("(\\s)+");
                    LOGGER.info("==tokens==={}",Lists.newArrayList(tokens));
                    for (int i=0; i<tokens.length; i++) {
                        String token = tokens[i];
                        if(sorceMap.containsKey(token) && sorceMap.get(token)>THREAD){  //这个词是触发词
                            //1. 词+词性
                            String result = token+"\t"+pos[i]+"\t";
                            //2. 左3词+词+右3词+左3词性+词性+右3词性
                            result += createStr(posList, i+3)+"\t";
                            //3. 获取左右实体
                            result += createEnty(tokens, i)+"\t";
                            //4. 同义词词林编码
                            result += createSampleCode(token)+"\t";
                            //5. 依存关系
                            result += DPParse.getDPType(sen, token, i)+"\t";
                            recogCount++;
                            String isTure = triggerForSentence.contains(token)?"Y":"N";
                            if("Y".equals(isTure)){
                                correctCount++;
                                triggerForSentence.remove(token);
                            }
                            bufferedWriter.write(result+isTure);
                            bufferedWriter.newLine();
                        }
                        i++;
                    }

                    sentenceIndex++;
                    count++;
                }

            }


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

    private static String createSampleCode(String token) {
        return SampWordParse.getSampleCode(token);
    }

    private static String createEnty(String[] tokens, int i) {

        return getLeftEnty(tokens, i) + getRightEnty(tokens, i);
    }

    private static String getLeftEnty(String[] tokens, int index) {
        return EntityFetch.getType(Lists.reverse(Lists.<String>newArrayList(tokens).subList(0, index)));
    }

    private static String getRightEnty(String[] tokens, int index) {
        return EntityFetch.getType(Lists.<String>newArrayList(tokens).subList(index+1, tokens.length));
    }

    private static String createStr(List<String> list, int index) {

        String wordStr = "";
        String posStr = "";

        for(int i=index-3; i<index; i++){
            wordStr+=list.get(i).split("/")[0]+"\t";
            posStr+=list.get(i).split("/")[1]+"\t";
        }

        wordStr+=list.get(index).split("/")[0]+"\t";
        posStr+=list.get(index).split("/")[1]+"\t";

        for(int i=index+1; i<=index+3; i++){
            wordStr+=list.get(i).split("/")[0]+"\t";
            posStr+=list.get(i).split("/")[1]+"\t";
        }

        return wordStr.trim()+"\t"+posStr.trim();
    }


}
