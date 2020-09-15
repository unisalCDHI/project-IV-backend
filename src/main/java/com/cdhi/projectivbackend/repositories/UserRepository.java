package com.cdhi.projectivbackend.repositories;

import com.cdhi.projectivbackend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Transactional(readOnly=true)
    User findByEmail(String email);

    @Transactional(readOnly=true)
    User findByUsername(String username);

    @Transactional(readOnly=true)
    List<User> findDistinctByNameContainingIgnoreCase(String name);

    @Transactional(readOnly = true)
    Page<User> findDistinctByNameContainingIgnoreCase(String name, Pageable pageRequest);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM KEYS", nativeQuery = true)
    List<Map<String, Integer>> findKeys();

    @Transactional(readOnly = true)
    List<User> findByIdIn(List<Integer> ids);

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM USER_ENTITY ue where LOWER(ue._NAME) like '%:nm%' and ue.ENABLED = TRUE",
            countQuery = "SELECT COUNT(*) FROM USER_ENTITY ue where LOWER(ue._NAME) like '%:nm%' and ue.ENABLED = TRUE",
            nativeQuery = true)
    Page<User> findAllPageable(@Param("nm") String name, Pageable pageable);
}
