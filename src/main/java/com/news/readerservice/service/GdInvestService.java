package com.news.readerservice.service;

import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.HttpClientUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GdInvestService extends GenericCrawlService{

    public Logger LOG = Logger.getLogger(PlatFormPreSaleService.class);

    public GdInvestService(NewsEntityService newsEntityService){
        super(newsEntityService);
    }

    @Override
    public void crawlPageUrlLink(List<String> destArray, String url, String websiteName, String pageCssSelect) {

        LOG.info("GdInvestService->crawlPageUrlLink");
        Document doc = null;
        try {
            doc = HttpClientUtil.getHtmlPageResponseAsDocument(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer maxPageNum = 0;

        Integer pageNumInt = 0;

        Elements els = doc.select(pageCssSelect);
        Iterator<Element> iter = els.iterator();
        while(iter.hasNext()){
            Element e = iter.next();
            Attributes attributes = e.attributes();
            String pageNumStr = attributes.get("href");
            LOG.info("pageNumStr===>"+pageNumStr);

            Pattern p = Pattern.compile("^.*jumpPage\\(([0-9]*)\\)$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(pageNumStr);
            if(m.matches()){
                pageNumInt = Integer.parseInt(m.group(1));
            }

            if(pageNumInt>maxPageNum){
                maxPageNum = pageNumInt;
            }
        }

        combinePageLinks(destArray, url, maxPageNum);

        LOG.info("maxPageNum-->"+maxPageNum);

    }

    public void combinePageLinks(List<String> pageLinksList, String baseUrl, int maxPageNum){
        for(int pageNum=1; pageNum<=maxPageNum && pageNum<=50; pageNum++){
            String url = HtmlUtil.getBaseUrlContext(baseUrl) + "?page.pageNo="+pageNum;
            LOG.info("combine url-->"+url);
            pageLinksList.add(url);
        }
    }
}
