package org.example.fanshop.repository;

import org.example.fanshop.entity.User;
import org.example.fanshop.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    List<UserImage> findByUser(User user);
    void deleteAllByUser(User user);
}
