package com.feng.fresh.api;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by feng on 2016/10/16.
 */
public class APIClient {

        public static final String UTF_8 = "UTF-8";
        private final static int TIMEOUT_CONNECTION = 30000;
        private final static int TIMEOUT_SOCKET = 30000;
        private final static int RETRY_TIME = 3;

        private static HttpClient getHttpClient() {
            HttpClient httpClient = new HttpClient();
            // 设置 默认的超时重试处理策略
            //httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            // 设置 连接超时时间
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
            // 设置 读数据超时时间
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
            // 设置 字符集
            httpClient.getParams().setContentCharset(UTF_8);
            return httpClient;
        }

        private static GetMethod getHttpGet(String url) {
            GetMethod httpGet = new GetMethod(url);
            // 设置 请求超时时间
            httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
            //httpGet.setRequestHeader("Host", "http://api.ltp-cloud.com/analysis/");
            httpGet.setRequestHeader("Connection","Keep-Alive");
            return httpGet;
        }
        private static PostMethod getHttpPost(String url) {
            PostMethod httpPost = new PostMethod(url);
            // 设置 请求超时时间
            httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
            //httpPost.setRequestHeader("Host", "http://api.ltp-cloud.com/analysis/");
            httpPost.setRequestHeader("Connection","Keep-Alive");
            return httpPost;
        }

        private static String _MakeURL(String p_url, Map<String, Object> params) {
            StringBuilder url = new StringBuilder(p_url);
            if(url.indexOf("?")<0)
                url.append('?');

            for(String name : params.keySet()){
                url.append('&');
                url.append(name);
                url.append('=');
                url.append(java.net.URLEncoder.encode(String.valueOf(params.get(name))));
            }
            //System.out.println("url="+url);
            return url.toString().replace("?&", "?");
        }

        private static String _post(String url, Map<String, Object> params) throws Exception
        {
            return _post(url, params, null);
        }

        /**
         * 通过post方法来获取输入流
         * @param url
         * @param params
         * @return
         * @throws Exception
         */
        private static String _post(String url, Map<String, Object> params, Map<String, File> fileParams) throws Exception
        {
            // TODO Auto-generated method stub
            System.out.println("post方法出");
            HttpClient httpClient = null;
            PostMethod httpPost = null;

            // post表单参数处理
            int paramLen = (params == null ? 0 : params.size());
            int fileLen = (fileParams == null ? 0 : fileParams.size());
            Part[] parts = new Part[paramLen + fileLen];
            int i = 0;
            if (params != null)
                for (String name : params.keySet()) {
                    parts[i++] = new StringPart(name, String.valueOf(params
                            .get(name)), UTF_8);
                    System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
                }

            if (fileParams != null)
            {
                for (String name : fileParams.keySet())
                {
                    parts[i++] = new FilePart(name, fileParams.get(name));
                    // System.out.println("post_key==> "+name+"    value==>"+fileParams.get(name).toString());
                }
            }

            String responseBody = "";
            int time = 0;

            try {
                httpClient = getHttpClient();
                //设置重发机制为不重发
                httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(0,false));
                httpPost = getHttpPost(url);
                System.out.println("Url"+url);
                httpPost.setRequestEntity(new MultipartRequestEntity(parts,
                        httpPost.getParams()));
                int statusCode = httpClient.executeMethod(httpPost);
                System.out.println("状态为："+statusCode);
                if (statusCode == HttpStatus.SC_OK) {// 连接成功
                    System.out.println("连接成功");
                    responseBody = httpPost.getResponseBodyAsString();
                } else {
                    System.out.println("连接失败");
                }
            } catch (HttpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
            //System.out.println(responseBody);
            return responseBody;
        }

        /**
         * 通过get方法获取数据流
         * @param url
         * @param params
         * @return
         * @throws Exception
         */
        public static String _get(String url,Map<String, Object> params) throws Exception
        {
            HttpClient httpClient = null;
            GetMethod httpGet = null;

            String responseBody = "";
            int time = 0;
            //int statusCode;
            do
            {
                try {
                    httpClient = getHttpClient();

                    httpGet =  new GetMethod(_MakeURL(url, params));
                    System.out.println("最终访问get方法的RUL:"+_MakeURL(url, params));


                    int statusCode = httpClient.executeMethod(httpGet);

                    if(statusCode == 200)
                    {
                        //System.out.println("Network connection succeeded! ");
                        responseBody = httpGet.getResponseBodyAsString();
                        break;
                        //return responseBody;
                    }
                    else
                    {
                        System.out.println("Network connection failded!");
                        time++;
                    }
                } catch (Exception e) {
                    time++;
                    System.out.println("重新连接");
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e1) {}
                    continue;
                }finally
                {
                    httpGet.releaseConnection();
                    httpClient = null;
                }
            }while(time < RETRY_TIME);
            return responseBody;

        }


        /*****防止重复提交的走这个*****/
        public static String _get(String url,Map<String, Object> params, int retryTime) throws Exception
        {
            HttpClient httpClient = null;
            GetMethod httpGet = null;

            String responseBody = "";
            int time = 0;
            //int statusCode;
            do
            {
                try {
                    httpClient = getHttpClient();

                    httpGet =  new GetMethod(_MakeURL(url, params));
                    System.out.println("最终访问get方法的RUL:"+_MakeURL(url, params));


                    int statusCode = httpClient.executeMethod(httpGet);

                    if(statusCode == 200)
                    {
                        System.out.println("Network connection succeeded! ");
                        responseBody = httpGet.getResponseBodyAsString();
                        break;
                        //return responseBody;
                    }
                    else
                    {
                        System.out.println("Network connection failded!");
                        time++;
                    }
                } catch (Exception e) {
                    time++;
                    System.out.println("重新连接");
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e1) {}
                    continue;
                }finally
                {
                    httpGet.releaseConnection();
                    httpClient = null;
                }
            }while(time < retryTime);
            return responseBody;

        }

    }
