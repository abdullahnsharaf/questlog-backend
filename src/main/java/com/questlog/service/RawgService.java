package com.questlog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class RawgService {

    @Value("${rawg.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api.rawg.io/api";

    // Search games by query
    public Map searchGames(String query, int page) {
        String url = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/games")
                .queryParam("key", apiKey)
                .queryParam("search", query)
                .queryParam("page", page)
                .queryParam("page_size", 20)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    // Get single game details
    public Map getGameById(int gameId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/games/" + gameId)
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    // Get trending games
    public Map getTrendingGames() {
        String url = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/games")
                .queryParam("key", apiKey)
                .queryParam("ordering", "-rating")
                .queryParam("page_size", 20)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    // Get new releases
    public Map getNewReleases() {
        String url = UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/games")
                .queryParam("key", apiKey)
                .queryParam("ordering", "-released")
                .queryParam("page_size", 20)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}