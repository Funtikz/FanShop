package org.example.fanshop.repository;

import org.example.fanshop.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository  extends JpaRepository<News, Long> {
}
