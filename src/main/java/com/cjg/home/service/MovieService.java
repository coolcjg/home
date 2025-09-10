package com.cjg.home.service;

import com.cjg.home.domain.CustomUserDetails;
import com.cjg.home.dto.request.MovieListRequestDto;
import com.cjg.home.dto.response.MovieListResponseDto;
import com.cjg.home.dto.response.MovieResponseDto;
import com.cjg.home.util.DateToString;
import com.cjg.home.util.PageUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MovieService {

    private final DateToString dateToString;

    @Value("${movie.api.key}")
    private String movieApiKey;

    public MovieListResponseDto list(MovieListRequestDto dto){

        MultiValueMap<String, String> params = dto.toParamMap();
        params.add("key",  movieApiKey);

        // URI 생성
        String uri = UriComponentsBuilder.fromUriString("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json")
                .queryParams(params)
                .toUriString();

        log.info("URI : " + uri);

        HttpResponse<String> response = null;

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Status Code: " + response.statusCode());
            log.info("Body: " + response.body());

        }catch(URISyntaxException | InterruptedException | IOException e){
            log.error(e.getMessage());

            return MovieListResponseDto.builder()
                    .code("error")
                    .build();
        }

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();

        if(obj.has("faultInfo")){
            log.error("API ERROR");
            log.error(obj);

            return MovieListResponseDto.builder()
                    .code("error")
                    .build();
        }else{

            JsonObject movieListResult = obj.get("movieListResult").getAsJsonObject();

            int totCnt = movieListResult.get("totCnt").getAsInt();

            JsonArray movieList = movieListResult.getAsJsonArray("movieList").getAsJsonArray();

            List<MovieResponseDto> list = new ArrayList<>();

            for(JsonElement e : movieList){
                JsonObject jo = e.getAsJsonObject();
                String movieCd = jo.get("movieCd").getAsString();
                String movieNm = jo.get("movieNm").getAsString();

                int prdtYear = jo.get("prdtYear").getAsInt();
                String openDt = jo.get("openDt").getAsString();

                String repNationNm = jo.get("repNationNm").getAsString();
                String repGenreNm = jo.get("repGenreNm").getAsString();

                String repDirector = "";
                if(!jo.get("directors").getAsJsonArray().isEmpty()){
                    repDirector = jo.get("directors").getAsJsonArray().get(0).getAsJsonObject().get("peopleNm").getAsString();
                }

                MovieResponseDto temp = MovieResponseDto.builder()
                        .movieCd(movieCd)
                        .movieNm(movieNm)
                        .prdtYear(prdtYear)
                        .openDt(openDt)
                        .repNationNm(repNationNm)
                        .repGenreNm(repGenreNm)
                        .repDirector(repDirector)
                        .build();
                list.add(temp);
            }


            int totPage = totCnt / dto.getItemPerPage();
            if(totPage == 0) totPage++;

            List<Integer> pageList = PageUtil.getStartEndPage(dto.getCurPage(), totPage);

            return MovieListResponseDto.builder()
                    .list(list)
                    .pageList(pageList)
                    .curPage(dto.getCurPage())
                    .totPage(totPage)
                    .totCnt(totCnt)
                    .itemPerPage(dto.getItemPerPage())
                    .movieNm(dto.getMovieNm())
                    .directorNm(dto.getDirectorNm())
                    .build();



        }


    }

    public MovieResponseDto view(CustomUserDetails customUserDetails, String movieCd){

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key",  movieApiKey);
        params.add("movieCd",  movieCd);

        // URI 생성
        String uri = UriComponentsBuilder.fromUriString("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json")
                .queryParams(params)
                .toUriString();

        log.info("URI : " + uri);

        HttpResponse<String> response = null;

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Status Code: " + response.statusCode());
            log.info("Body: " + response.body());

        }catch(URISyntaxException | InterruptedException | IOException e){
            log.error(e.getMessage());
        }

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();

        JsonObject movieInfo = obj.get("movieInfoResult").getAsJsonObject().get("movieInfo").getAsJsonObject();

        String movieNm = movieInfo.get("movieNm").getAsString();
        int showTm = movieInfo.get("showTm").getAsInt();
        int prdtYear = movieInfo.get("prdtYear").getAsInt();
        String openDt = movieInfo.get("openDt").getAsString();
        String prdtStatNm = movieInfo.get("prdtStatNm").getAsString();
        String typeNm = movieInfo.get("typeNm").getAsString();

        List<String> nations = new ArrayList<>();

        for(JsonElement e : movieInfo.get("nations").getAsJsonArray()){
            nations.add(e.getAsJsonObject().get("nationNm").getAsString());
        }

        List<String> genres = new ArrayList<>();

        for(JsonElement e : movieInfo.get("genres").getAsJsonArray()){
            genres.add(e.getAsJsonObject().get("genreNm").getAsString());
        }

        List<String> directors = new ArrayList<>();

        for(JsonElement e : movieInfo.get("directors").getAsJsonArray()){
            directors.add(e.getAsJsonObject().get("peopleNm").getAsString());
        }

        List<String> actors = new ArrayList<>();

        for(JsonElement e : movieInfo.get("actors").getAsJsonArray()){
            actors.add(e.getAsJsonObject().get("peopleNm").getAsString());
        }

        MovieResponseDto dto = MovieResponseDto.builder()
                .movieCd(movieCd)
                .movieNm(movieNm)
                .showTm(showTm)
                .prdtYear(prdtYear)
                .openDt(openDt)
                .prdtStatNm(prdtStatNm)
                .typeNm(typeNm)
                .nations(nations)
                .genres(genres)
                .directors(directors)
                .actors(actors)
                .build();

        return dto;
    }
}

