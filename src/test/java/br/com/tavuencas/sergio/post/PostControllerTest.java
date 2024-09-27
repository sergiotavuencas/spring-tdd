package br.com.tavuencas.sergio.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository repository;

    List<Post> posts;

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1, 1, "Hello, World!", "This is my first post.", null),
                new Post(2, 2, "Second Post", "This is my second post.", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post.",
                        "version":null
                    },
                    {
                        "id":2,
                        "userId":2,
                        "title":"Second Post",
                        "body":"This is my second post.",
                        "version":null
                    }
                ]
                """;

        when(repository.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        var json = """
                {
                    "id":1,
                    "userId":1,
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "version":null
                }
                """;

        when(repository.findById(1)).thenReturn(Optional.of(posts.getFirst()));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(repository.findById(0)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get("/api/posts/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenPostIsValid() throws Exception {
        var json = """
                {
                    "id":1,
                    "userId":1,
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "version":null
                }
                """;

        when(repository.save(posts.getFirst())).thenReturn(posts.getFirst());

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreatePostWhenPostIsInvalid() throws Exception {
        var invalidPost = new Post(3, 1, "", "", null);
        var json = """
                {
                    "id":3,
                    "userId":1,
                    "title":"",
                    "body":"",
                    "version":null
                }
                """;

        when(repository.save(invalidPost)).thenReturn(invalidPost);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        var updatedPost = new Post(1, 1, "Hello, Space!", "This is my new first post.", null);
        var json = """
                {
                    "id":1,
                    "userId":1,
                    "title":"Hello, Space!",
                    "body":"This is my new first post.",
                    "version":null
                }
                """;

        when(repository.findById(posts.getFirst().id())).thenReturn(Optional.of(posts.getFirst()));
        when(repository.save(updatedPost)).thenReturn(updatedPost);

        mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePostWhenGivenValidId() throws Exception {
        doNothing().when(repository).deleteById(posts.getFirst().id());

        mockMvc.perform(delete("/api/posts/1")).andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1);
    }
}
