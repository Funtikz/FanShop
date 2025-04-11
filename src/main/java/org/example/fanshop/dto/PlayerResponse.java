package org.example.fanshop.dto;

import lombok.Data;
import org.example.fanshop.entity.enums.ClubRole;
import org.example.fanshop.entity.enums.PlayerPosition;

@Data
public class PlayerResponse {
    private Long playerId;
    private String firstname;
    private String lastname;
    private ClubRole role;
    private PlayerPosition playerPosition;
    private Long imageId;
}
