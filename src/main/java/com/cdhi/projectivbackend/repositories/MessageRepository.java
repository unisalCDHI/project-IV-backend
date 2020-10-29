package com.cdhi.projectivbackend.repositories;

import com.cdhi.projectivbackend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM MESSAGES m WHERE m.sender_id = :snd AND m.recipient_id = :rcp ORDER BY created_date ASC",
            nativeQuery = true)
    List<Message> findConversationBetween(@Param("snd") Integer sender_id, @Param("rcp") Integer recipient_id);
}
