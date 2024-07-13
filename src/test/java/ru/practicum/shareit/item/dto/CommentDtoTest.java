package ru.practicum.shareit.item.dto;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> tester;

    @Test
    void test() throws IOException {
        CommentDto commentDto = new CommentDto()
                .setText("Лучший товар");
        JsonContent<CommentDto> result = tester.write(commentDto);
        assertThat(result).isNotNull().hasJsonPath("$.text");
    }
}