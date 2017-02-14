package com.fitnesse.parallel; /**
 * Created by ykhandelwal on 1/11/17.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

/**
 * Example program to list links from a URL.
 */
public class ListLinks {
    public static void main(String[] args) throws IOException {
        //Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
//        String url = "http://localhost:8888/FitNesse.TestsSuite.RestApis.TestsClientType.AwsE2eTests.yogesh";
        print("Fetching %s...", url);

        if(Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .execute().statusCode() != 200) {
            System.out.println("Given Url :" + url + "is not a valid url. Hence Aborting the run....");
            exit(0);
        }

        Document doc = Jsoup.connect(url).get();

        Elements links;
        ArrayList<String> tests = new ArrayList<String>();
        Set<String> allPages= new HashSet<String>();

        com.fitnesse.parallel.FitnesseExecutor fe;
        fe = new com.fitnesse.parallel.FitnesseExecutor();
        //System.out.println(doc.select("a.suite").size());
        if(doc.select("a.suite").size() != 0) {
            links = doc.select("a.suite");
            ArrayList<String> arrayList = new ArrayList<String>();
            for (Element link : links) {
                arrayList.add(link.attr("abs:href"));
            }
            allPages=getAllTestPagesToRun(arrayList);
            print("\nTestPage Count: (%d)", allPages.size());
            ArrayList<String> objects = new ArrayList<String>(allPages);
            objects.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if(o1.contains("UserFi") || o1.contains("AccountSyncAndProvisioning") || o1.contains("AccountsRetrySuite"))
                        return -1;
                    else
                        return 0;
                }
            });
            fe.execute(objects,Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }
        else if(doc.select("a.test").size()!=0){
            links = doc.select("a.test");
            for (Element element : links) {
                tests.add(element.attr("abs:href")+"?test&format=xml&includehtml");
                print(element.attr("abs:href"), trim(element.text(), 35));
            }
            fe.execute(tests,Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }
        else{
            System.out.println("\nGiven URL does not contain any SUITE or TEST. Please correct the URL and retry.");
            exit(0);
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    private static Set<String> getAllTestPagesToRun(List<String> suites) throws IOException {
        Set<String> tests = new HashSet<String>();
        for (String suit : suites) {
            Document doc = Jsoup.connect(suit).get();
            Elements links = doc.select("a.test");
            for (Element element : links) {
                tests.add(element.attr("abs:href")+"?test&format=xml&includehtml");
               print(element.attr("abs:href"), trim(element.text(), 35));
            }
        }
        return tests;
    }
}

