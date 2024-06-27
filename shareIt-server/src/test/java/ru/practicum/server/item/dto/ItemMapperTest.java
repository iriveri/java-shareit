package ru.practicum.server.item.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.ExtendedItem;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.common.item.dto.ExtendedItemDto;
import ru.practicum.common.item.dto.ItemDto;
import ru.practicum.server.booking.mapper.BookingMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;


    @TestConfiguration
    static class Config {
        @Bean
        public ItemMapper itemMapper() {
            return Mappers.getMapper(ItemMapper.class);
        }

        @Bean
        public CommentMapper commentMapper() {
            return Mappers.getMapper(CommentMapper.class);
        }

        @Bean
        public BookingMapper bookingMapper() {
            return Mappers.getMapper(BookingMapper.class);
        }
    }

    @Test
    void testToDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setDescription("ItemDescription");
        item.setAvailable(true);

        ItemDto dto = itemMapper.toDto(item);
        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    void testToItem() {
        ItemDto dto = new ItemDto(1L, "ItemName", "ItemDescription", true, 2L);
        Item item = itemMapper.toItem(dto);
        assertNotNull(item);
        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }

    @Test
    void testToExtendedDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setDescription("ItemDescription");
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        user.setName("UserName");

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(LocalDateTime.now().plusDays(1));
        lastBooking.setBooker(user);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().plusDays(2));
        nextBooking.setEnd(LocalDateTime.now().plusDays(3));
        nextBooking.setBooker(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("CommentText");
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());

        ExtendedItem extendedItem = new ExtendedItem(item);
        extendedItem.setLastBooking(lastBooking);
        extendedItem.setNextBooking(nextBooking);
        extendedItem.setComments(List.of(comment));

        ExtendedItemDto extendedDto = itemMapper.toExtendedDto(extendedItem);
        assertNotNull(extendedDto);
        assertEquals(extendedItem.getId(), extendedDto.getId());
        assertEquals(extendedItem.getName(), extendedDto.getName());
        assertEquals(extendedItem.getDescription(), extendedDto.getDescription());
        assertEquals(extendedItem.getAvailable(), extendedDto.getAvailable());

        assertNotNull(extendedDto.getLastBooking());
        assertEquals(lastBooking.getId(), extendedDto.getLastBooking().getId());

        assertNotNull(extendedDto.getNextBooking());
        assertEquals(nextBooking.getId(), extendedDto.getNextBooking().getId());

        assertNotNull(extendedDto.getComments());
        assertThat(extendedDto.getComments()).hasSize(1);
        assertEquals(comment.getId(), extendedDto.getComments().iterator().next().getId());
        assertEquals(comment.getText(), extendedDto.getComments().iterator().next().getText());
    }
}
