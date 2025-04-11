package org.example.fanshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.PlayerRequest;
import org.example.fanshop.dto.PlayerResponse;
import org.example.fanshop.entity.Player;
import org.example.fanshop.entity.enums.ClubRole;
import org.example.fanshop.entity.enums.PlayerPosition;
import org.example.fanshop.service.impl.PlayerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerServiceImpl playerService;

    @PostMapping
    public ResponseEntity<Void> createPlayer(@RequestParam String firstname,
                                           @RequestParam String lastname,
                                           @RequestParam ClubRole role,
                                           @RequestParam(required = false) PlayerPosition playerPosition,
                                           @RequestParam MultipartFile image) {
        PlayerRequest playerRequest = new PlayerRequest();
        playerRequest.setFirstname(firstname);
        playerRequest.setLastname(lastname);
        playerRequest.setRole(role);
        playerRequest.setPlayerPosition(playerPosition);
        playerRequest.setImage(image);
        playerService.addPlayerWithImage(playerRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlayer(@PathVariable("id") Long id,
                                           @RequestParam String firstname,
                                           @RequestParam String lastname,
                                           @RequestParam ClubRole role,
                                           @RequestParam(required = false) PlayerPosition playerPosition,
                                           @RequestParam MultipartFile image) {

        PlayerResponse response = new PlayerResponse();
        Player player = playerService.findById(id);
        response.setFirstname(firstname);
        response.setImageId(player.getImage().getId());
        response.setLastname(lastname);
        response.setRole(role);
        response.setPlayerPosition(playerPosition);
        response.setPlayerId(id);

        playerService.updatePlayer(response, image);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayerById(id);
        return ResponseEntity.ok("Player successfully deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.findAll());
    }
}
