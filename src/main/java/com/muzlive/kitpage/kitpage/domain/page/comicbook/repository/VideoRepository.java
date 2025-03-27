package com.muzlive.kitpage.kitpage.domain.page.comicbook.repository;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	@Query("SELECT v FROM Video v JOIN FETCH v.image WHERE v.pageUid = :pageUid")
	List<Video> findByPageUid(Long pageUid);
}
