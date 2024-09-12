package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
}
