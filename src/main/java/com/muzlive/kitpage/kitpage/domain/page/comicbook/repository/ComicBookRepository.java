package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicBookRepository extends JpaRepository<ComicBook, Long> {
}
