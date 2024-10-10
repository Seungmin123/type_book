package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "install_log")
@Entity
public class InstallLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "install_log_uid", nullable = false)
	private Long installLogUid;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "serial_number", nullable = false)
	private String serialNumber;

	@Column(name = "kit_uid", nullable = false)
	private Long kitUid;

	@Column(name = "page_uid", nullable = false)
	private Long pageUid;

	@Builder
	public InstallLog(String deviceId, String serialNumber, Long kitUid, Long pageUid) {
		this.deviceId = deviceId;
		this.serialNumber = serialNumber;
		this.kitUid = kitUid;
		this.pageUid = pageUid;
	}

	public InstallLog(KitLog kitLog) {
		this.deviceId = kitLog.getDeviceId();
		this.serialNumber = kitLog.getSerialNumber();
		this.kitUid = kitLog.getKitUid();
		this.pageUid = kitLog.getPageUid();
	}
}
