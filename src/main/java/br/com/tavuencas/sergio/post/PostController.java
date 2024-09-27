package br.com.tavuencas.sergio.post;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostRepository repository;

    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<Post> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(repository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Post create(@RequestBody @Validated Post request) {
        return repository.save(request);
    }

    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @RequestBody @Validated Post request) {
        Optional<Post> existing = repository.findById(id);

        if (existing.isPresent()) {
            Post updated = new Post(
                    existing.get().id(),
                    existing.get().userId(),
                    request.title(),
                    request.body(),
                    existing.get().version()
            );

            return repository.save(updated);
        } else {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
