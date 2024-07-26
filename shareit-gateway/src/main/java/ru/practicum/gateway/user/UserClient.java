package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return postWithPathAndBody("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Long userId) {
        return patchWithPathAndBody("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return getWithPath("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return getWithPath("");
    }

    public ResponseEntity<Object> deleteUserById(Long userId) {
       return deleteWithPath("/" + userId);
    }
}