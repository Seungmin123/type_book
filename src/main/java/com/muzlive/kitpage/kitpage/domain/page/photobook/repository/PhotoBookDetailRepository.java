package com.muzlive.kitpage.kitpage.domain.page.photobook.repository;

import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoBookDetailRepository extends JpaRepository<PhotoBookDetail, Long> {

	@Query("SELECT MAX(pbd.page) FROM PhotoBookDetail pbd WHERE pbd.photoBookUid = :photoBookUid")
	Integer findMaxPage(Long photoBookUid);

}
