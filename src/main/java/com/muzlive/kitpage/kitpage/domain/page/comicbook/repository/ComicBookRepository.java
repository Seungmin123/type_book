package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicBookRepository extends JpaRepository<ComicBook, Long> {

	Optional<List<ComicBook>> findByPageContentId(String contentId);
}
