package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.Kit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

	Optional<Kit> findBySerialNumber(String serialNumber);

	boolean existsBySerialNumber(String serialNumber);

	Optional<Kit> findByDeviceIdAndSerialNumber(String deviceId, String serialNumber);

	Optional<List<Kit>> findByDeviceId(String deviceId);

	Optional<Kit> findByDeviceIdAndPageUid(String deviceId, Long pageUid);
}
