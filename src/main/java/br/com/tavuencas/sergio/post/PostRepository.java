package br.com.tavuencas.sergio.post;

import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<Post, Integer> {
}
