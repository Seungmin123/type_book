package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.MemberLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberLogRepository extends JpaRepository<MemberLog, Long> {
}
