package com.news.readerservice.utils;


import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    public static final Logger LOG= Logger.getLogger(HttpClientUtil.class.getName());

    private static PoolingHttpClientConnectionManager connectionManager;
    private static HttpClientConnectionMonitorThread thread;


    public static HttpClient createMultiThreadClient(int maxCon, int maxConPerRoute,int connectionTimeout,int soTimeout){
        if(connectionManager==null) {
            connectionManager = new PoolingHttpClientConnectionManager();
        // 整个连接池最大连接数
        connectionManager.setMaxTotal(maxCon);
        // 每路由最大连接数，默认值是2
        connectionManager.setDefaultMaxPerRoute(maxConPerRoute);
        /** 管理 http连接池 */
        thread = new HttpClientConnectionMonitorThread(connectionManager);
        }

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(1000).setSocketTimeout(soTimeout).setRedirectsEnabled(false).build();

        HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).setRetryHandler(createMyRetryHandler()).build();

        return httpClient;
    }

    public static HttpClient createDefaultClient(){
        return createMultiThreadClient(400, 60, 5000, 9000);
    }



    public static HttpRequestRetryHandler createMyRetryHandler(){
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int excutionCount, HttpContext context) {
                if(excutionCount>=3){
                    return false;
                }

                if(e instanceof NoHttpResponseException){
                    return true;
                }
                if(e instanceof SSLHandshakeException){
                    return false;
                }
                HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }

                return false;
            }
        };
        return myRetryHandler;
    }

    public static String crawl(String name, HttpClient httpClient, String url, String encoding){
        String webPageContent = null;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.63 Safari/534.3");
        httpGet.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");

        HttpEntity entity =null;

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();

            if(code==200){
                entity = response.getEntity();

                if(entity!=null){
                    webPageContent = EntityUtils.toString(entity, encoding);
                    LOG.info("[Website "+name+"] download url " + url + " success");
                    EntityUtils.consume(entity);
                    httpGet.abort();
                    System.out.println("webPageContent-->"+webPageContent);
                    return webPageContent;
                }
            }

            if(entity!=null) {
                EntityUtils.consume(entity);
            }
            httpGet.abort();

            if(code==500) {
                return Constants.SERVER_BUSY;
            } else if(code==404) {
                return Constants.NOT_FOUNDSTR;
            } else if(code==302) {
                return "302";
            } else {
                return null;
            }


        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("[Website "+name+"] try download failed " + url);
            if(entity!=null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                    LOG.warn("",e);
                }
            }
            httpGet.abort();
        }

        return webPageContent;
    }



    public static String httpPost(List<NameValuePair> dataList, String url, String encoding){
        String webPageContent = "";
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(createMyRetryHandler()).build();
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000).setConnectTimeout(1000).setSocketTimeout(30000).build();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new UrlEncodedFormEntity(dataList,Consts.UTF_8));

        try {
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();

            HttpEntity httpEntity = null;
            if(code==200){
                httpEntity = response.getEntity();
                webPageContent = EntityUtils.toString(httpEntity, encoding);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                httpPost.abort();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return webPageContent;
    }



    public static void main(String[] args){
        HttpClient httpClient = HttpClientUtil.createMultiThreadClient(400,60,5000,9000);
        String content = HttpClientUtil.crawl("珠海市住房", httpClient, "http://zjj.zhuhai.gov.cn/zwgk/jfwj/", Consts.UTF_8.toString());
        System.out.println(content);

    }


}

class HttpClientConnectionMonitorThread extends Thread {

    private final HttpClientConnectionManager connManager;
    private volatile boolean shutdown = false;

    public HttpClientConnectionMonitorThread(HttpClientConnectionManager connManager) {
        super();
        this.setName("http-connection-monitor");
        this.setDaemon(true);
        this.connManager = connManager;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    // 等待5秒
                    wait(5000);
                    // 关闭过期的链接
                    connManager.closeExpiredConnections();
                    // 选择关闭 空闲30秒的链接
                    connManager.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            //ignore ex
        }
    }
}
