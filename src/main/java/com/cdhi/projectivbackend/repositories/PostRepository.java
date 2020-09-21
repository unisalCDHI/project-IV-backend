package com.cdhi.projectivbackend.repositories;

import com.cdhi.projectivbackend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Transactional(readOnly=true)
    List<Post> findAllByOwner_id(Integer ownerId);

}
