package ru.practicum.shareit.server.item.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.common.item.dto.CommentDto;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;


    @TestConfiguration
    static class Config {
        @Bean
        public CommentMapper commentMapper() {
            return Mappers.getMapper(CommentMapper.class);
        }
    }

    @Test
    void testToDto() {
        User user = new User();
        user.setName("AuthorName");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("CommentText");
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = commentMapper.toDto(comment);
        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(user.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void testToComment() {
        CommentDto dto = new CommentDto(1L, "CommentText", "AuthorName", LocalDateTime.now());
        Comment comment = commentMapper.toComment(dto);
        assertNotNull(comment);
        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getText());
        assertEquals(dto.getAuthorName(), comment.getUser().getName());
        assertEquals(dto.getCreated(), comment.getCreated());
    }
}
