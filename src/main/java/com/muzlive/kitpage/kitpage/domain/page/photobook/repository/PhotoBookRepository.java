package com.muzlive.kitpage.kitpage.domain.page.photobook.repository;

import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoBookRepository extends JpaRepository<PhotoBook, Long> {

}
