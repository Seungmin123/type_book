package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

	Optional<List<Page>> findAllByContentId(String contentId);

	Optional<Page> findFirstByOrderByPageUidDesc();

	@Query("SELECT DISTINCT p "
		+ "FROM Page p "
		+ "JOIN FETCH p.content "
		+ "LEFT JOIN FETCH p.comicBooks "
		+ "WHERE p.contentId = :contentId")
	List<Page> findAllWithComicBooks(String contentId);

	@EntityGraph(attributePaths = {"comicBooks"})
	@Query("SELECT DISTINCT p "
		+ "FROM Page p "
		+ "INNER JOIN Kit k ON k.pageUid = p.pageUid "
		+ "WHERE k.serialNumber = :serialNumber")
	Optional<Page> findWithChildBySerialNumber(String serialNumber);

	Optional<Page> findByAlbumId(String albumId);
}
