package com.news.readerservice.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    public static int getPageNum(String url){
        int pageNum = 0;
        String regx_url = "(^[^_]*)_([0-9]*)\\.(html)$";
        Pattern p = Pattern.compile(regx_url, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        if(matcher.find()){
            pageNum = Integer.parseInt(matcher.group(2));
        }
        System.out.println("pageNum-->"+pageNum);
        return pageNum;
    }

    public static void main(String[] args){

        HtmlUtil.getPageNum("http://zjj.zhuhai.gov.cn/zwgk/jfwj/index_2.html");
    }
}
