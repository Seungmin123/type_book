package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Content;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

	@Query("SELECT c FROM Content c LEFT JOIN FETCH c.pages p WHERE c.contentId = :contentId AND c.region = :region")
	Optional<Content> findByContentIdAndRegion(String contentId, Region region);
}
