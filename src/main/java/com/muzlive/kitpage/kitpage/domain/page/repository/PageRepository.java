package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

	Optional<Page> findFirstByOrderByPageUidDesc();

	@Query(value = "SELECT content_id FROM content WHERE content_id LIKE CONCAT('KP_', :prefix, '_%') ORDER BY content_id DESC LIMIT 1", nativeQuery = true)
	Optional<String> findMaxContentIdByPrefix(@Param("prefix") String prefix);

	@Query(value = "SELECT album_id FROM page WHERE album_id LIKE CONCAT(:prefix, '_%') ORDER BY album_id DESC LIMIT 1", nativeQuery = true)
	Optional<String> findMaxAlbumIdByPrefix(@Param("prefix") String contentId);

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

	@Query("SELECT p "
		+ "FROM Page p "
		+ "INNER JOIN Kit k ON k.pageUid = p.pageUid "
		+ "WHERE k.serialNumber = :serialNumber")
	Optional<Page> findBySerialNumber(String serialNumber);

	Optional<Page> findByAlbumId(String albumId);
}
