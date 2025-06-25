package com.muzlive.kitpage.kitpage.domain.user.repository;

import com.muzlive.kitpage.kitpage.domain.user.Kit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

	Optional<Kit> findBySerialNumber(String serialNumber);

	boolean existsBySerialNumber(String serialNumber);

	Optional<Kit> findByDeviceIdAndSerialNumber(String deviceId, String serialNumber);

	Optional<List<Kit>> findByDeviceId(String deviceId);

	Optional<Kit> findByDeviceIdAndPageUid(String deviceId, Long pageUid);

	@Modifying(clearAutomatically = true)
	@Query(value = ""
		+ "UPDATE kit k "
		+ "INNER JOIN page p ON p.page_uid = k.page_uid "
		+ "SET k.device_id = '' "
		+ "WHERE k.device_id = :deviceId "
		+ "AND p.content_id = :contentId", nativeQuery = true)
	void deleteKitMappingByContentId(@Param("deviceId") String deviceId, @Param("contentId") String contentId);
}
