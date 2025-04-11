package org.example.fanshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.fanshop.entity.enums.ClubRole;
import org.example.fanshop.entity.enums.PlayerPosition;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private ClubRole role;
    private PlayerPosition playerPosition;
    @OneToOne(cascade = CascadeType.ALL)
    private Image image;
}
