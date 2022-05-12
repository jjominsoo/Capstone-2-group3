package com.kakao.cafe.repository;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.dto.PreviewArticleResponse;
import com.kakao.cafe.mapper.ArticleMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;


@Repository
public class ArticleRepository {
    private final JdbcTemplate jdbcTemplate;

    public ArticleRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long save(Article article) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into ARTICLE_TABLE (WRITER, TITLE, CONTENTS, TIME) values (?,?,?,now())", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getWriter());
            ps.setString(2, article.getTitle());
            ps.setString(3, article.getContents());
            return ps;
        }, keyHolder);
        return (Long)keyHolder.getKey();
    }

    public List<PreviewArticleResponse> findAll() {
        final String sql = "SELECT article.id, " +
                "article.title," +
                "article.writer," +
                "article.time," +
                "FROM ARTICLE_TABLE as article";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> PreviewArticleResponse.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .writer(rs.getString("writer"))
                        .time(rs.getString("time"))
                        .build()
        );
    }

    public Optional<Article> findById(Long id) {
        return jdbcTemplate.
                query("select * from ARTICLE_TABLE where id = ?", ArticleMapper.INSTANCE, id).
                stream().findAny();

    }

    public void updateArticle(Long id, Article updateArticle) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE ARTICLE_TABLE SET TITLE = ?, CONTENTS = ? where id = ?");
            ps.setString(1, updateArticle.getTitle());
            ps.setString(2, updateArticle.getContents());
            ps.setString(3, String.valueOf(id));
            return ps;
        });
    }

    public void deleteArticle(Long id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM ARTICLE_TABLE where id = ?");
            ps.setString(1, String.valueOf(id));
            return ps;
        });
    }


}
