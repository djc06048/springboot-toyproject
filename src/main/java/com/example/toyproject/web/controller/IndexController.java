package com.example.toyproject.web.controller;

import com.example.toyproject.config.auth.LoginUser;
import com.example.toyproject.config.auth.dto.SessionUser;
import com.example.toyproject.domain.posts.Posts;
import com.example.toyproject.domain.user.User;
import com.example.toyproject.domain.user.UserRepository;
import com.example.toyproject.service.comments.CommentService;
import com.example.toyproject.service.posts.PostsService;
import com.example.toyproject.web.dto.comments.CommentResponseDto;
import com.example.toyproject.web.dto.comments.CommentUpdateRequestDto;
import com.example.toyproject.web.dto.posts.PostsResponseDto;
import com.example.toyproject.web.utils.WrongUserExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class IndexController {
    /**
     * post 관련 컨트롤러
     */
    private final PostsService postsService;



    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){
        //등록된 모든 글 보여주기(홈화면)
        model.addAttribute("posts",postsService.findAllDesc());
        if (user != null) {
            log.info("현재 로그인된 유저" + user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userPicture", user.getPicture());
        }
        //로그인 안되어있을 경우 mustache에서 처리함
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(Model model,@LoginUser SessionUser user) {
        if (user != null) {
            //로그인이 된 사용자의 경우 글 등록페이지로 이동
            log.info("로그인이 된 사용자 입니다");
            model.addAttribute("user", user);
            model.addAttribute("userPicture", user.getPicture());
            return "posts-save";
        } else {
            //로그인이 안된 사용자의 경우 홈화면으로 리다이렉트
            //TODO: 리다이렉트아닌, 경고창 띄어주기
            log.info("로그인이 필요합니다");
            model.addAttribute("posts", postsService.findAllDesc());
            return "redirect:/";
        }


    }


    @GetMapping("/posts/update/{postId}")
    public String postsUpdate(@PathVariable Long postId, Model model,@LoginUser SessionUser user) {
        if (user != null) {
            log.info("로그인이 된 사용자 입니다");
            model.addAttribute("userPicture", user.getPicture());
            Boolean isValidate = postsService.checkValidUpdateUser(postsService.findByPostId(postId).getUserId(), user.getUserId(), postId);
            if (isValidate.equals(true)) {
                PostsResponseDto dto = postsService.findByPostId(postId);
                model.addAttribute("post", dto);
                return "posts-update";
            } else {
                //글을 작성한 사용자가 아닌 경우, index 페이지로 리다이렉트
                //TODO: 리다이렉트아닌, 경고창 띄어주기
                model.addAttribute("posts", postsService.findAllDesc());
                return "redirect:/";
            }
        } else {
            //로그인이 안된 사용자인 경우, index 페이지로 리다이렉트
            //TODO: 리다이렉트아닌, 경고창 띄어주기
            log.info("로그인이 필요합니다");
            model.addAttribute("posts", postsService.findAllDesc());
            return "redirect:/";
        }
    }



    /**
     * search
     * 최신순으로 정렬
     */
    @GetMapping("/posts/search")
    public String search(String keyword, Model model, @LoginUser SessionUser user, @PageableDefault(size=3,sort = "lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("userPicture", user.getPicture());
        }
        Page<Posts> searchList = postsService.search(keyword,pageable);
        model.addAttribute("searchList", searchList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        model.addAttribute("hasNext", searchList.hasNext());
        model.addAttribute("hasPrev", searchList.hasPrevious());

        return "posts-search";
    }


}
