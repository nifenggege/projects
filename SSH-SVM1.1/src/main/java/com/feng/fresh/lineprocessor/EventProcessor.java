package com.feng.fresh.lineprocessor;

import com.feng.fresh.model.Constant;
import com.feng.fresh.model.Event;
import com.feng.fresh.model.EventEnum;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.LineProcessor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取每行中的触发次
 * 1. 分句
 * 2. 触发词--触发词类型：word2typeMap
 * 3. 触发词类型-个数：type2countMap
 * 4. 触发词-个数： word2countMap
 * Created by feng on 2016/8/25.
 */
public class EventProcessor implements LineProcessor<Map<EventEnum, Map<String, Integer>>>{

    private Map<EventEnum, Map<String, Integer>> triggerMap = Maps.newHashMap();

    private List<List<String>> sentenceTriggerList = Lists.newArrayList();

    public  Map<String, EventEnum> word2typeMap = Maps.newHashMap();

    public boolean processLine(String s) throws IOException {

        if(StringUtils.isEmpty(s)) return false;
        String[] sentences = s.trim().split(Constant.SPLIT_FOR_SENTENCE);
        for(String sentence : sentences){
           // System.out.println(sentence);
            filterEvent(sentence);
        }
        return true;
    }

    private void filterEvent(String s) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(s),"句子不能为空");

        List<String> list = Lists.newArrayList();
        String reg_pattern = "(?<=<((\\w){1,20})-trigger>)[^<>]*(?=<\\/\\1-trigger>)";
        Pattern pattern = Pattern.compile(reg_pattern);
        Matcher matcher = pattern.matcher(s);

        while(matcher.find())
        {
            String triggerWord = matcher.group();
            EventEnum type = EventEnum.valueOf(matcher.group(1));
            increaseTriggerCount(triggerWord, type);
            list.add(triggerWord);
            word2typeMap.put(triggerWord, type);
        }

        sentenceTriggerList.add(list);
    }

    public void increaseTriggerCount(String triggerWord, EventEnum type){
        Map<String, Integer> triggerCountMap = triggerMap.get(type);
        if(null == triggerCountMap){
            triggerCountMap = Maps.newHashMap();
            triggerCountMap.put(triggerWord, 1);
        }
        else{
            if(triggerCountMap.containsKey(triggerWord)){
                triggerCountMap.put(triggerWord, triggerCountMap.get(triggerWord)+1);
            }
            else{
                triggerCountMap.put(triggerWord, 1);
            }
        }
        triggerMap.put(type, triggerCountMap);
    }

    public Map<EventEnum, Map<String, Integer>> getResult() {
        return triggerMap;
    }

    public List<List<String>> getSentenceTriggerList() {
        return sentenceTriggerList;
    }

    public Map<String, EventEnum> getWord2typeMap() {
        return word2typeMap;
    }

}
