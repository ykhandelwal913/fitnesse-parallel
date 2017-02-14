package com.fitnesse.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.sun.jersey.api.client.Client;
import org.springframework.retry.annotation.Retryable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FitnesseExecutor {
    static Client client = Client.create();
    public static  BlockingQueue<Runnable> getDocumentsQueue=null;
    public static  ExecutorService getDocumentsExecutorService=null;

    public static void execute (ArrayList<String> elements, int min, int max) {


        getDocumentsQueue = new ArrayBlockingQueue<Runnable>(1000);
        getDocumentsExecutorService = new ThreadPoolExecutor(min, max,
                10, TimeUnit.SECONDS, getDocumentsQueue);
        Long startTime= System.currentTimeMillis();
        System.out.println("startTime: "+startTime);
        //Create fixed Thread pool, here pool of 2 thread will created
        //ExecutorService pool = Executors.newFixedThreadPool(30);
        for(String s :elements){
            getDocumentsExecutorService.submit(new SomeRunnable(s));
        }

        new Runnable() {
            @Override
            public void run(){
                  try {
                      while(! getDocumentsQueue.isEmpty()) {
                          System.out.println("queue is not empty: "+getDocumentsQueue.size());
                          Thread.sleep(10000);
                      }

                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  finally{
                      try {
                          getDocumentsExecutorService.awaitTermination(1,TimeUnit.MINUTES);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      getDocumentsExecutorService.shutdown();
                      Long endTime= System.currentTimeMillis();
                      System.out.println("endTime: "+endTime);
                      System.exit(0);
                  }
            }
        }.run();


//        pool.execute(obj1);
//        pool.execute(obj2);


    }
    static class SomeRunnable implements Runnable {
        private String uri;
        private final String USER_AGENT = "Mozilla/5.0";
        public SomeRunnable(String uri){
                this.uri=uri;
        }
        int threadNo = -1 ;
        List<String> list = new ArrayList<String>();
        public SomeRunnable(List list, int threadNo ) {
            this.list.addAll(list);
            this.threadNo =threadNo;
        }
        @Override
        public void run() {



//                WebResource resource = client.resource(uri);
//                System.out.println("url: "+uri);
//
//                 ClientResponse cr= resource.header("Accept","text/html,application/xhtml+xml,application/xml").header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36").get(ClientResponse.class);
//
//                System.out.println(cr.getStatus());
                try {
                    sendGet(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

        // HTTP GET request
        @Retryable(include = {Exception.class, Throwable.class})
        private void sendGet(String url) throws Exception {

          //  String url = "http://www.google.com/search?q=mkyong";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            con.setConnectTimeout(3600000);
            con.setReadTimeout(3600000);
            con.setDoOutput(true);

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            //System.out.println(response.toString());

        }
    }

}