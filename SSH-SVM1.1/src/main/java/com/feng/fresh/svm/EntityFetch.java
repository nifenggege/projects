package com.feng.fresh.svm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by feng on 2016/10/27.
 */
public class EntityFetch {

    private static Map<String, String>  entityMap = Maps.newHashMap();

    static{
        String trainPath = EntityFetch.class.getClassLoader().getResource("train/train-arg.txt").getPath();
        String testPath = EntityFetch.class.getClassLoader().getResource("train/test-arg.txt").getPath();
        parseTxt(trainPath);
        parseTxt(testPath);
    }

    public static void parseTxt(String name){

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
            String line = "";

            String reg = "<([a-zA-Z]*)-Arg>([^<]*)";
            Pattern pattern = Pattern.compile(reg);

            while((line=br.readLine()) != null){

                Matcher matcher = pattern.matcher(line);
                while(matcher.find()){
                    if(matcher.group(1).equals("Event")){
                        continue;
                    }
                    entityMap.put(matcher.group(2), matcher.group(1));
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getType(List<String> list){
        for(int i=0; i<list.size(); i++){
            if(entityMap.containsKey(list.get(i))){
                return entityMap.get(list.get(i));
            }
        }
        return "null";
    }
    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<String, String> entityMap) {
        this.entityMap = entityMap;
    }

    public static void main(String[] args) {
        EntityFetch entityFetch = new EntityFetch();
        String trainPath = entityFetch.getClass().getClassLoader().getResource("train/train-arg.txt").getPath();
        String testPath = entityFetch.getClass().getClassLoader().getResource("train/test-arg.txt").getPath();
        entityFetch.parseTxt(trainPath);
        entityFetch.parseTxt(testPath);
        System.out.println(entityFetch.entityMap);
    }
}
