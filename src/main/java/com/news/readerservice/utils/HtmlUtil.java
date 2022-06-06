package com.news.readerservice.utils;

import com.news.readerservice.inter.NewEntityMapper;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    private static Logger LOG = Logger.getLogger(HtmlUtil.class);

    public static String convertLink(String baseUrl, String url){
        String link = "";
        if(url.indexOf("http")>-1){
            link = url;
        }else{
            link = getBaseUrl(baseUrl) + "/" + url;
        }
        return link;
    }

    public static String getBaseUrl(String url){
        String base = "";
        if(url.indexOf("/")>-1){
            base = url.substring(0, url.lastIndexOf("/"));
        }else{
            base = url;
        }

        LOG.info("baseurl ==>"+base);
        return base;
    }

    public static String getBaseUrlContext(String url){
        String base = getBaseUrl(url);
        if(url==null){
            return "";
        }
        if(url.indexOf("?")>-1){
            return base + url.substring(url.lastIndexOf("/"), url.indexOf("?"));
        }
        return url.trim();
    }

    public static int getPageNum(String url){
        int pageNum = 0;
        String regx_url = "(^[^_]*)_([0-9]*)\\.(html)$";
        Pattern p = Pattern.compile(regx_url, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        if(matcher.find()){
            pageNum = Integer.parseInt(matcher.group(2));
        }
//        System.out.println("pageNum-->"+pageNum);
        return pageNum;
    }

    public static int getMaxPageNum(List<String> linksList){
        int maxPageNum = 0;
        Pattern p = Pattern.compile("http://.*?_([0-9]*)\\.html", Pattern.CASE_INSENSITIVE);
        Matcher m = null;
        for(String url : linksList){
            m = p.matcher(url);
            LOG.info("url-->"+url);
            if(m.find()){
                Integer pageNum = Integer.parseInt(m.group(1));
                if(maxPageNum<pageNum){
                    maxPageNum = pageNum;
                }
            }

        }
//        LOG.info("maxPageNum-->"+maxPageNum);
        return maxPageNum;
    }

    public static List<String> getFullPageList(List<String> source){
        List<String> pageLinkList = new ArrayList<>();
        int maxPageNum = getMaxPageNum(source);
            for(String link : source){
                String templateUrl = "";
                if(link.split("_").length>1){
                    templateUrl = link.split("_")[0] + "_" + "{pageSize}.html";
                }else{
                    templateUrl = link.substring(0,link.lastIndexOf(".")) + "_" + "{pageSize}.html";
                }
                for(int i=1; i<=maxPageNum; i++){
                    String pageUrl = templateUrl.replace("{pageSize}", String.valueOf(i));
//                    if(HttpClientUtil.healthCheckUrl(pageUrl)){
                    if(!source.contains(pageUrl)){
                            source.add(pageUrl);
                    }
                }

                break;
            }




        LOG.info("source==>"+source);

        return source;

    }


    public static List<NewsEntity> crawlTableToNewsEnttiy(String tableContent, String pageUrl, WebSiteEntity webSiteEntity){
        Pattern p = Pattern.compile(webSiteEntity.getNewsTagRegex(), Pattern.CASE_INSENSITIVE);
//        LOG.info("tableContent-->"+tableContent);
//        LOG.info("webSiteEntity-->"+webSiteEntity);
        Matcher m = p.matcher(tableContent);
        return webSiteEntity.getMapper().mapToList(m, pageUrl,webSiteEntity);
    }

    public static List<NewsEntity> crawlDocToNewsEntity(Document doc, String pageUrl, WebSiteEntity webSiteEntity){
        return webSiteEntity.getDocMapper().mapToList(doc,pageUrl,webSiteEntity);
    }

    public static void main(String[] args){

        HtmlUtil.getPageNum("http://www.zhzx.gov.cn/tagz/taxd/");

        String newsDiv  = "<div class=\"list\" style=\"margin-top:20px;\">\n" +
                "\t\t  <ul>\n" +
                "\t\t  \t\n" +
                "\t\t\t\t\t<li>\n" +
                "\t\t\t\t\t\t<span>2020-09-15 </span>\n" +
                "\t\t\t\t\t\t<a href=\"./202009/t20200915_58784841.html\" title=\"关于推动珠海先进制造业与现代服务业深度融合发展的建议\">\n" +
                "\t\t\t\t\t\t\t关于推动珠海先进制造业与现代服务业深度融合发展的建议\n" +
                "\t\t\t\t\t\t</a>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t  </ul>\n" +
                "      </div>";
        String pageUrl = "http://localhost";
        WebSiteEntity webSiteEntity = new WebSiteEntity("测试","http://www.a.com");
//        webSiteEntity.setNewsTagRegex("<td[^>]*?><a href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a></td><td[^>]*?>([^<]*?)</td>");
        webSiteEntity.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a href=\"([^\"]*?)\"[^>]*?title=\"([^\"]*?)\">[^<]*?</a>[^<]*?</li>");
        webSiteEntity.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setPageUrl(pageUrl);
                    resultList.add(newsEntity);
                }
                return resultList;
            }
        });
        HtmlUtil.crawlTableToNewsEnttiy(newsDiv, pageUrl, webSiteEntity);
        System.out.println(HtmlUtil.crawlTableToNewsEnttiy(newsDiv, pageUrl, webSiteEntity));
    }
}
