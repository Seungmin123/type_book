package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicBookDetailRepository extends JpaRepository<ComicBookDetail, Long> {

	@Query("SELECT MAX(cbd.page) FROM ComicBookDetail cbd WHERE cbd.comicBookUid = :comicBookUid")
	Integer findMaxPage(Long comicBookUid);
}
