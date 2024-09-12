package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
}
