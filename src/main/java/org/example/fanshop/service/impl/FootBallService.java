package org.example.fanshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return laligaTable.getBody();
        }
        laligaTable = response;
        return response.getBody();
    }

    public String getFinishedMatches(){
        RestTemplate restTemplate = new RestTemplate();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime pastDays = currentTime.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String fromDate = pastDays.format(formatter);
        String toDate = currentTime.format(formatter);
        String URL = String.format("https://api.football-data.org/v4/teams/94/matches?status=FINISHED&dateFrom=%s&dateTo=%s&limit=5", fromDate, toDate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return lastMatches.getBody();
        }
        lastMatches = response;
        return response.getBody();
    }

    public String getNextMatches(){
        RestTemplate restTemplate = new RestTemplate();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime pastDays = currentTime.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String fromDate = currentTime.format(formatter);
        String toDate = pastDays.format(formatter);
        String URL = String.format("https://api.football-data.org/v4/teams/94/matches?status=SCHEDULED&dateFrom=%s&dateTo=%s&limit=5", fromDate, toDate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", API_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return nextMatches.getBody();
        }
        nextMatches = response;
        return response.getBody();
    }
}
