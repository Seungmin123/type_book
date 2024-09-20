package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "kit_log")
@Entity
public class KitLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kit_log_uid", nullable = false)
	private Long kitLogUid;

	@Column(name = "kit_uid", nullable = false)
	private Long kitUid;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Builder
	public KitLog(Long kitUid, String deviceId) {
		this.kitUid = kitUid;
		this.deviceId = deviceId;
	}

	public static KitLog of(Kit kit) {
		return KitLog.builder()
			.kitUid(kit.getKitUid())
			.deviceId(kit.getDeviceId())
			.build();
	}
}
