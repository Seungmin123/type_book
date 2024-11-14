package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

	Optional<List<Page>> findAllByContentIdAndRegion(String contentId, Region region);

	Optional<Page> findFirstByOrderByPageUidDesc();

	@EntityGraph(attributePaths = {"comicBooks"})
	@Query("SELECT DISTINCT p "
		+ "FROM Page p "
		+ "WHERE p.contentId = :contentId AND p.region = :region")
	Optional<List<Page>> findAllWithChild(String contentId, Region region);

	@EntityGraph(attributePaths = {"comicBooks"})
	@Query("SELECT DISTINCT p "
		+ "FROM Page p "
		+ "INNER JOIN Kit k ON k.pageUid = p.pageUid "
		+ "WHERE k.serialNumber = :serialNumber")
	Optional<Page> findWithChildBySerialNumber(String serialNumber);
}
