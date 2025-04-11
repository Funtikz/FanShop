package org.example.fanshop.service.impl;


import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.PlayerRequest;
import org.example.fanshop.dto.PlayerResponse;
import org.example.fanshop.entity.Image;
import org.example.fanshop.entity.Player;
import org.example.fanshop.minio.MinioService;
import org.example.fanshop.repository.ImageRepository;
import org.example.fanshop.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class PlayerServiceImpl {
    private final PlayerRepository playerRepository;
    private final MinioService minioService;
    private final ImageRepository imageRepository;

    @Transactional
    public void addPlayerWithImage(PlayerRequest request) {
        try {
            Image image = minioService.uploadSingleImage(request.getImage());
            Player player = new Player();
            player.setPlayerPosition(request.getPlayerPosition());
            player.setImage(image);
            player.setRole(request.getRole());
            player.setLastname(request.getLastname());
            player.setFirstname(request.getFirstname());
            playerRepository.save(player);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении новости с изображением", e);
        }
    }

    @Transactional
    public void updatePlayer(PlayerResponse response, MultipartFile newImage) {
        Player player = findById(response.getPlayerId());

        player.setPlayerPosition(response.getPlayerPosition());
        player.setRole(response.getRole());
        player.setLastname(response.getLastname());
        player.setFirstname(response.getFirstname());


        if (newImage != null && !newImage.isEmpty()) {
            try {
                Image oldImage = player.getImage();
                if (oldImage != null) {
                    minioService.deleteImage(oldImage.getMinioId());
                    imageRepository.delete(oldImage);
                }

                Image newImageEntity = minioService.uploadSingleImage(newImage);

                player.setImage(newImageEntity);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при обновлении изображения", e);
            }
        }

        playerRepository.save(player);
    }

    @Transactional
    public void deletePlayerById(Long id){
        Player byId = findById(id);
        minioService.deleteImage(byId.getImage().getMinioId());
        playerRepository.delete(byId);
    }

    public Player findById(Long id){
        return playerRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }

    public List<Player> findAll(){
        return  playerRepository.findAll();
    }

}
