package com.news.readerservice.service;

import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;
import java.util.List;

public class PlatFormPreSaleService  extends  GenericCrawlService{

    public Logger LOG = Logger.getLogger(PlatFormPreSaleService.class);

    public PlatFormPreSaleService(NewsEntityService newsEntityService) {
        super(newsEntityService);
    }


    @Override
    public void crawlPageUrlLink(List<String> destArray, String url, String websiteName, String pageCssSelect) {

        LOG.info("PlatFormPreSaleService->crawlPageUrlLink");
        Document doc = null;
        try {
            doc = HttpClientUtil.getHtmlPageResponseAsDocument(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Elements els = doc.select(".pagenav>a[href]");

        Iterator<Element> iter = els.iterator();

        Integer maxPageNum = 0;
        while(iter.hasNext()){
            Element link = iter.next();
            String pageNumStr = StringUtils.trim(link.text());
            LOG.info("pageNumStr-->"+pageNumStr);
            if(pageNumStr.matches("^[0-9]*$")){
                Integer pageNum = Integer.parseInt(pageNumStr);
                if(pageNum>maxPageNum){
                    maxPageNum = pageNum;
                }
            }
        }
        LOG.info("maxPageNum-->"+maxPageNum);

        combinePageLinks(destArray, url, maxPageNum, 10, "keywords=presale&tabkey=all&searchcode=");



    }


    public void combinePageLinks(List<String> pageLinksList, String baseUrl, int maxPageNum, int pageSize, String queryStr){
        for(int pageNum=1; pageNum<=maxPageNum; pageNum++){
            String url = HtmlUtil.getBaseUrlContext(baseUrl) + "?" + queryStr + "&start="+pageNum + "&count=" + pageSize;
            LOG.info("combine url-->"+url);
            pageLinksList.add(url);
        }
    }




}
