package com.example.toyproject.web.controller.comments;


import com.example.toyproject.domain.comments.Comments;
import com.example.toyproject.domain.comments.CommentsRepository;
import com.example.toyproject.domain.posts.PostsRepository;

import com.example.toyproject.domain.user.UserRepository;
import com.example.toyproject.service.comments.CommentService;

import com.example.toyproject.web.dto.comments.CommentSaveRequestDto;
import com.example.toyproject.web.dto.comments.CommentUpdateRequestDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private CommentService commentService;
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    //MockMvc: ?????? ????????? ??????????????? ???????????? ????????? ????????? ????????? ??????
    //????????? ????????? ?????????????????? ????????? ???????????? ????????? ????????? MVC ?????????
    //????????? ??? ?????? ?????????

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) //????????? ??????????????? Spring MVC ???????????? ????????? ??? ????????? ?????? ?????? ????????? ??????
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @Transactional
    public void saveComment() throws Exception {
        //given
        String content = "api ???????????? ??????";
        Long postId = 1L;
        Long userId = 1L;


        CommentSaveRequestDto requestDto = CommentSaveRequestDto
                .builder()
                .content(content)
                .postId(postId)
                .userId(userId)
                .build();

        String url = "http://localhost:" + port + "/api/v1/comments";

        //when
        System.out.println(" ###INSERT START");
        ResultActions actions = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        System.out.println(" ###INSERT END");

        //then
        actions.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());


    }

    @Test
    @WithMockUser(roles = "USER")
    @Transactional
    public void updateComment() throws Exception {
        String content = "api ???????????? ?????? ????????????";
        Long userId = 1L;
        Long postId = 5L;

        CommentUpdateRequestDto requestDto = CommentUpdateRequestDto
                .builder()
                .content(content)
                .build();

        String url = "http://localhost:" + port + "/api/v1/comments";

        //when
        System.out.println(" ###UPDATE START");
        ResultActions actions = mvc.perform(put(url + "/posts/{postId}/users/{userId}", postId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        System.out.println(" ###UPDATE END");

        //then
        actions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value(content))

                .andDo(MockMvcResultHandlers.print());

        List<Comments> all = commentsRepository.findAll();
        assertThat(all.get(10).getContent()).isEqualTo(content);

    }
}