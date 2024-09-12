package com.muzlive.kitpage.kitpage.domain.page.repository;

import com.muzlive.kitpage.kitpage.domain.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
}
