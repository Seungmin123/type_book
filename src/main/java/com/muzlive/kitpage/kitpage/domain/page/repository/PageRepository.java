package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

	Optional<List<Page>> findAllByContentIdAndRegion(String contentId, Region region);

	Optional<Page> findFirstByOrderByPageUidDesc();
}
