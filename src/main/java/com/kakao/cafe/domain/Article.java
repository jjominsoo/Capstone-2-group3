package com.kakao.cafe.domain;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Article {
    /* ArticleRepository 저장시 pk 위해서 id 변수 생성*/
    private Long id;
    private String writer;
    private String title;
    private String contents;
    private String time;
}
