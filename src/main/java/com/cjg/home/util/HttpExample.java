package com.cjg.home.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpExample {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=6ba8f440278b968d5580cfa38055b7da"))
                .GET() // 기본값은 GET
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
