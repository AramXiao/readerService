package com.news.readerservice.service;

import com.news.readerservice.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeCenterPlaceService extends GenericCrawlService{

    public TradeCenterPlaceService(NewsEntityService newsEntityService) {
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
        Elements els = doc.select(pageCssSelect);
        Integer maxPageNum = 0;
        if(els.size()>0){
            for(Element e : els){
                String onclick = StringUtils.trim(e.attr("onclick"));

                Pattern p = Pattern.compile(".*index_([0-9]*)\\.jhtml.*", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(onclick);
                String pageNumStr = "";

                if(m.find()){
                    pageNumStr = m.group(1);
                    Integer pageNum = Integer.parseInt(pageNumStr);
                    if(pageNum>maxPageNum){
                        maxPageNum = pageNum;
                    }
                }

            }
        }

        //组装分页url
        String pageUrlTemplate = "http://ggzy.zhuhai.gov.cn/exchangeinfo/landexchange/tdjygg/index_%d.jhtml";

        for(int i=1; i<maxPageNum; i++){
            String pageUrl =  String.format(pageUrlTemplate, i);
            destArray.add(pageUrl);
        }



    }
}
