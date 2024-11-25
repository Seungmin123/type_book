package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicBookRepository extends JpaRepository<ComicBook, Long> {

	Optional<List<ComicBook>> findByPageContentId(String contentId);

	@EntityGraph(attributePaths = {"comicBookDetails", "comicBookDetails.image"})
	@Query("SELECT DISTINCT cb FROM ComicBook cb WHERE cb.pageUid = :pageUid")
	Optional<List<ComicBook>> findAllByPageUid(Long pageUid);
}
