package com.example.toyproject.web.controller.posts;

import com.example.toyproject.domain.posts.Posts;
import com.example.toyproject.domain.posts.PostsRepository;
import com.example.toyproject.domain.user.UserRepository;
import com.example.toyproject.web.dto.posts.PostsResponseDto;
import com.example.toyproject.web.dto.posts.PostsSaveRequestDto;
import com.example.toyproject.web.dto.posts.PostsUpdateRequestDto;
import com.example.toyproject.web.utils.WrongPostsExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UserRepository usersRepository;

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
    public void save() throws Exception {
        //given
        String title = "???????????? ?????????";
        String content = "???????????? ??? ??????";
        Long userId = 1L;

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

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
    public void update() throws Exception {
        //given
        Long postId = 1L;
        Long userId = 1L;
        String title = "???????????? ??? ???????????? ??????";
        String content = "???????????? ??? ???????????? ??????";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(title)
                .content(content)
                .build();


        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        System.out.println(" ###UPDATE START");

        ResultActions actions = mvc.perform(put(url + "/{postId}/users/{userId}", postId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        System.out.println(" ###UPDATE END");

        //then
        actions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(content))
                .andDo(MockMvcResultHandlers.print());

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
    }


    @Test
    @WithMockUser(roles = "USER")
    @Transactional
    public void findByPostId() throws Exception {
        Long postId = 5L;
        String url = "http://localhost:" + port + "/api/v1/posts";

        mvc.perform(get(url + "/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());


    }
}
