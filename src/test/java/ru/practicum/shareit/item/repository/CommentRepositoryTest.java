package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User()
                .setName("Петр")
                .setEmail("petr@mail.ru"));

        item = itemRepository.save(new Item()
                .setName("Кружка")
                .setOwner(user)
                .setAvailable(true)
                .setDescription("Цвет синий, материал металл"));
    }

    @Test
    void findAllByItemIdIfEmpty() {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());

        assertTrue(comments.isEmpty());
    }

    @Test
    void findAllByItemId() {
        Comment comment = new Comment()
                .setItem(item)
                .setCreated(LocalDateTime.now())
                .setText("Отличная кружка")
                .setAuthor(user);
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        assertFalse(comments.isEmpty());
        assertEquals(comments.size(), 1);
        assertEquals(comments.get(0).getId(), comment.getId());
        assertEquals(comments.get(0).getText(), comment.getText());
        assertEquals(comments.get(0).getCreated(), comment.getCreated());
        assertEquals(comments.get(0).getAuthor(), comment.getAuthor());
    }
}