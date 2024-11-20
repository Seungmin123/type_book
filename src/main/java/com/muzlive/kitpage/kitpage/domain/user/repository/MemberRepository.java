package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.Member;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByDeviceId(String deviceId);

	@QueryHints({
		@QueryHint(name = "javax.persistence.lock.timeout", value = "3000") // 5초
	})
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Member m WHERE m.deviceId = :deviceId")
	Optional<Member> findByDeviceIdWithLock(String deviceId);

	Optional<Member> findByDeviceIdAndEmail(String deviceId, String email);
}
