package org.example.fanshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FootBallService {

    private static final String API_URL = "https://api.football-data.org/v4/competitions/PD/standings";
    private static final String API_TOKEN = "53d917cb0e09408b8b831ba133fb9db1";
    private ResponseEntity<String> laligaTable;
    private ResponseEntity<String> lastMatches;

    private ResponseEntity<String> nextMatches;

    public String getLaLigaTable() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
            laligaTable = response;
            return response.getBody();
        } catch (HttpClientErrorException.TooManyRequests e) {
            if (laligaTable != null) {
                return laligaTable.getBody();
            } else {
                return "{\"error\":\"Too many requests and no cached data available\"}";
            }
        }
    }

    public String getFinishedMatches() {
        RestTemplate restTemplate = new RestTemplate();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime pastDays = currentTime.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String fromDate = pastDays.format(formatter);
        String toDate = currentTime.format(formatter);
        String URL = String.format(
                "https://api.football-data.org/v4/teams/94/matches?status=FINISHED&dateFrom=%s&dateTo=%s&limit=5",
                fromDate, toDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
            lastMatches = response; // кэшируем
            return response.getBody();
        } catch (HttpClientErrorException.TooManyRequests e) {
            if (lastMatches != null) {
                return lastMatches.getBody();
            } else {
                return "{\"error\":\"Too many requests and no cached data available\"}";
            }
        }
    }
    public String getNextMatches() {
        RestTemplate restTemplate = new RestTemplate();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime futureTime = currentTime.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String fromDate = currentTime.format(formatter);
        String toDate = futureTime.format(formatter);
        String URL = String.format(
                "https://api.football-data.org/v4/teams/94/matches?status=SCHEDULED&dateFrom=%s&dateTo=%s&limit=5",
                fromDate, toDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
            nextMatches = response; // кэшируем
            return response.getBody();
        } catch (HttpClientErrorException.TooManyRequests e) {
            if (nextMatches != null) {
                return nextMatches.getBody();
            } else {
                return "{\"error\":\"Too many requests and no cached data available\"}";
            }
        }
    }

}
