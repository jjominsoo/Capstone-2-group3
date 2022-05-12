package com.kakao.cafe.controller;

import com.kakao.cafe.domain.Article;
import com.kakao.cafe.domain.User;
import com.kakao.cafe.dto.ArticleFormRequest;
import com.kakao.cafe.dto.PreviewArticleResponse;
import com.kakao.cafe.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String findArticleList(Model model) {
        List<PreviewArticleResponse> articles = articleService.findArticleList();
        model.addAttribute("articles", articles);
        return "qna/list";
    }

    @PostMapping("/questions")
    public String create(ArticleFormRequest articleFormRequest, HttpSession session) {
        Object value = session.getAttribute("sessionedUser");

        articleService.save(articleFormRequest, ((User) value).getName());
        return "redirect:/";
    }

    @PutMapping("/questions/{id}")
    public String update(@PathVariable("id") Long id, ArticleFormRequest articleFormRequest, HttpSession session) {
        Object value = session.getAttribute("sessionedUser");
        User sessionUser = (User) value;
        try {
            articleService.updateArticleInfo(id, sessionUser, articleFormRequest);
        } catch (AuthenticationException e) {
            return "redirect:/articles/" + id;
        }
        return "redirect:/";
    }

    @DeleteMapping("/questions/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        Object value = session.getAttribute("sessionedUser");
        User sessionUser = (User) value;
        try {
            articleService.deleteArticle(id, sessionUser);
        } catch (AuthenticationException e) {
            return "redirect:/articles/" + id;
        }
        return "redirect:/";
    }
    @GetMapping("/questions/{id}/form")
    public String updateForm(@PathVariable("id") Long articleId, Model model, HttpSession session) {
        Object value = session.getAttribute("sessionedUser");
        Article article = articleService.findArticle(articleId);
        model.addAttribute("article", article);
        return "qna/updateform";
    }

    @GetMapping("/articles/{id}")
    public String findArticle(@PathVariable("id") Long articleId, Model model, HttpSession session) {
        Article article = articleService.findArticle(articleId);

        Object value = session.getAttribute("sessionedUser");

        model.addAttribute("article", article);
        return "qna/show";
    }


}
