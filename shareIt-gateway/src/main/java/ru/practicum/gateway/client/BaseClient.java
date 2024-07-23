package ru.practicum.gateway.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    private final RestTemplate rest;

    public BaseClient(final RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> getWithPath(String path) {
        return getWithFullArguments(path, null, null);
    }

    protected ResponseEntity<Object> getWithPathAndUserId(String path, long userId) {
        return getWithFullArguments(path, userId, null);
    }

    protected ResponseEntity<Object> getWithFullArguments(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> postWithPathAndBody(String path, T body) {
        return postWithFullArguments(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> postWithPathAndUserIdAndBody(String path, long userId, T body) {
        return postWithFullArguments(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> postWithFullArguments(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> putWithPathAndBody(String path, long userId, T body) {
        return putWithFullArguments(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> putWithFullArguments(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patchWithPathAndUserId(String path, long userId) {
        return patchFullArguments(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patchWithPathAndBody(String path, T body) {
        return patchFullArguments(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patchWithPathAndUserIdAndBody(String path, long userId, T body) {
        return patchFullArguments(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patchFullArguments(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> deleteWithPath(String path) {
        return deleteWithFullArguments(path, null, null);
    }

    protected ResponseEntity<Object> deleteWithPathAndUserId(String path, long userId) {
        return deleteWithFullArguments(path, userId, null);
    }

    protected ResponseEntity<Object> deleteWithFullArguments(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}