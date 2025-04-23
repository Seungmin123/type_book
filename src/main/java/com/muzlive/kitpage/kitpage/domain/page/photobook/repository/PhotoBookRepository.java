package com.muzlive.kitpage.kitpage.domain.page.photobook.repository;

import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoBookRepository extends JpaRepository<PhotoBook, Long> {

	@EntityGraph(attributePaths = {"photoBookDetails", "photoBookDetails.image"})
	@Query("SELECT DISTINCT pb FROM PhotoBook pb WHERE pb.pageUid = :pageUid")
	Optional<List<PhotoBook>> findAllByPageUid(Long pageUid);

	@Query("SELECT SUM(i.imageSize) "
		+ "FROM PhotoBook pb "
		+ "JOIN pb.photoBookDetails pbd "
		+ "JOIN pbd.image i "
		+ "WHERE pb.page.pageUid = :pageUid")
	Optional<Long> sumImageSizeByPageUid(Long pageUid);

}
