package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitLogRepository extends JpaRepository<KitLog, Long> {

	Optional<KitLog> findFirstByDeviceIdAndSerialNumberOrderByKitLogUidDesc(String deviceId, String serialNumber);
}
