package org.example.fanshop.controller;


import lombok.RequiredArgsConstructor;
import org.example.fanshop.service.impl.FootBallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService laLigaService;
    @GetMapping("/laliga")
    public String getLaLigaTable() {
        return laLigaService.getLaLigaTable();
    }

    @GetMapping("last")
    public String getLatestMatches(){
        return laLigaService.getFinishedMatches();
    }

    @GetMapping("next")
    public  String getNextMatches(){
        return laLigaService.getNextMatches();
    }
}
