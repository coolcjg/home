package com.cjg.home.service;

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

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Body: " + response.body());

        }catch(URISyntaxException | InterruptedException | IOException e){
            log.error(e.getMessage());
        }

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();

        JsonObject movieListResult = obj.get("movieListResult").getAsJsonObject();

        int totCnt = movieListResult.get("totCnt").getAsInt();

        JsonArray movieList = movieListResult.getAsJsonArray("movieList").getAsJsonArray();

        List<MovieResponseDto> list = new ArrayList<>();

        for(JsonElement e : movieList){
            JsonObject jo = e.getAsJsonObject();
            String movieCd = jo.get("movieCd").getAsString();
            String movieNm = jo.get("movieNm").getAsString();

            String prdtYear = jo.get("prdtYear").getAsString();
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

    public String getQueryParams(MovieListRequestDto dto, int pageNumber){

        StringBuilder sb = new StringBuilder();
        sb.append("/movie/list?");

//        if(dto.getSearchType() != null){
//            sb.append("searchType=").append(dto.getSearchType()).append("&");
//        }
//
//        if(dto.getSearchText() != null){
//            sb.append("searchText=").append(dto.getSearchText()).append("&");
//        }

        sb.append("curPage=").append(pageNumber).append("&");
        sb.append("itemPerPage=").append(dto.getItemPerPage()).append("&");

        if(sb.lastIndexOf("&") == sb.length()-1){
            sb.delete(sb.length()-1, sb.length());
        }

        return sb.toString();

    };


//    public PostResponseDto view(CustomUserDetails customUserDetails, Long postId){
//        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomViewException(ResultCode.POST_SEARCH_NOT_FOUND));
//        if(post.getOpen() == 'Y'){
//            post.setViewCnt(post.getViewCnt()+1);
//            return postToDto(post);
//        }else{
//            if(customUserDetails == null){
//                throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
//            }else{
//                if(auth.isSameUserForUser(customUserDetails, post.getUser().getUserId())){
//                    post.setViewCnt(post.getViewCnt()+1);
//                    return postToDto(post);
//                }else{
//                    throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
//                }
//            }
//        }
//    }

//    public PostResponseDto postToDto(Post post){
//        return PostResponseDto.builder()
//                .postId(post.getPostId())
//                .userId(post.getUser().getUserId())
//                .name(aes256.decrypt(post.getUser().getName()))
//                .image(imageUrlPrefix + post.getUser().getImage())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .open(post.getOpen())
//                .viewCnt(post.getViewCnt())
//                .commentResponseDtoList(commentListToDto(commentRepository.recursiveList(post.getPostId())))
//                .regDate(dateToString.apply(post.getRegDate()))
//                .modDate(dateToString.apply(post.getModDate()))
//                .build();
//    }

}

