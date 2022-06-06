package com.news.readerservice.service;

import com.news.readerservice.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZhuHaiCityBuildService extends GenericCrawlService{


    public ZhuHaiCityBuildService(NewsEntityService newsEntityService) {
        super(newsEntityService);
    }

    @Override
    public void crawlPageUrlLink(List<String> destArray, String url, String websiteName, String pageCssSelect) {
        //获取网页内容


        Document doc = null;
        try {
            doc = HttpClientUtil.getHtmlPageResponseAsDocument(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Elements els = doc.select(".lispage>span");
        Integer totalPage = 0;
        if(els.size()>0){
            for(Element e : els){
                String text = StringUtils.trim(e.text());
                Pattern p = Pattern.compile("共([0-9]*)页", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(text);
                String totalPageStr = "";

                if(m.find()){
                    totalPageStr = m.group(1);
                    totalPage = Integer.parseInt(totalPageStr);
                    break;
                }

            }
        }

        //组装分页url
        String pageUrlTemplate = "http://219.131.222.110:8899/cbms/zhzgj/toPermitSearchsCbmsWZAction.action?evalpage=%d&identity=permit&cbmsPermitWZ.permitNo=&cbmsPermitWZ.projectName=&cbmsPermitWZ.cunitJS=&cbmsPermitWZ.cunitSG=";

        for(int i=1; i<totalPage; i++){
           String pageUrl =  String.format(pageUrlTemplate, i);
           destArray.add(pageUrl);
        }

    }
}
