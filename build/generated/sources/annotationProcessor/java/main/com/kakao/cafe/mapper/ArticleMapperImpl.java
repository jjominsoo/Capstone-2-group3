package com.kakao.cafe.mapper;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.domain.Article.ArticleBuilder;
import com.kakao.cafe.dto.ArticleFormRequest;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-13T00:49:14+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.3.2.jar, environment: Java 11.0.14.1 (Amazon.com Inc.)"
)
public class ArticleMapperImpl implements ArticleMapper {

    @Override
    public Article toEntity(ArticleFormRequest articleFormRequest) {
        if ( articleFormRequest == null ) {
            return null;
        }

        ArticleBuilder article = Article.builder();

        article.title( articleFormRequest.getTitle() );
        article.contents( articleFormRequest.getContents() );

        return article.build();
    }
}
