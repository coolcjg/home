package com.cjg.home.dto.request;



import com.cjg.home.code.ResultCode;
import com.cjg.home.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class PostListRequestDto {

    private String title;
    private String content;
    private String userId;
    private String open;

    private Integer pageNumber;
    private Integer pageSize;

    private String searchType;
    private String searchText;

    public void checkParam(){

        if(searchText != null){
            switch (searchType) {
                case "title" -> title = searchText;
                case "content" -> content = searchText;
                case "userId" -> userId = searchText;
            }
        }

        if(title != null && title.isBlank()){
            throw new CustomException(ResultCode.POST_INVALID_TITLE);
        }

        if(content != null && content.isBlank()){
            throw new CustomException(ResultCode.POST_INVALID_CONTENT);
        }

        if(open != null && (open.length() > 1 || open.isBlank())  && !open.equals("Y") && !open.equals("N")){
            throw new CustomException(ResultCode.POST_INVALID_OPEN);
        }

        if(pageNumber <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_NUMBER);
        }

        if(pageSize <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_SIZE);
        }
    }
}
