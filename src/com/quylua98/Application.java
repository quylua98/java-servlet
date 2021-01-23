package com.quylua98;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Application extends HttpServlet {

    public void readFile(StringBuilder sb) throws IOException {
        InputStream is = getServletContext().getResourceAsStream("/resource/go-docs.txt");
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isReader);
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        is.close();
        isReader.close();
        reader.close();
    }

    public static String get() {
        StringBuilder res = new StringBuilder();
        try {
            URL server = new URL("http://localhost:9069/test");
            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                res.append(line);
            }
            rd.close();
            connection.disconnect();
            return res.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Future<String> promise() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            String response = get();
            completableFuture.complete(response);
            return null;
        });

        return completableFuture;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
//        StringBuilder testStr = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            testStr.append(Math.abs(i * i));
//        }

        StringBuilder sb = new StringBuilder();

//        readFile(sb);
//        readFile(sb);

        Future<String> p1 = this.promise();
        Future<String> p2 = this.promise();

        try {
            sb.append(p1.get()).append(p2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.println(sb.toString());
    }
}
