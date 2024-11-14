package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenLogRepository extends JpaRepository<TokenLog, Long> {

	Optional<TokenLog> findFirstByDeviceIdAndTypeOrderByCreatedAtDesc(String deviceId, TokenType tokenType);
}
