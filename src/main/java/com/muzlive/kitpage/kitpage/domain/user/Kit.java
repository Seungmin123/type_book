package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "kit")
@Entity
public class Kit extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kit_uid", nullable = false)
	private Long kitUid;

	@Setter
	@Column(name = "serial_number")
	private String serialNumber;

	@Setter
	@Column(name = "device_id")
	private String deviceId;

	@Setter
	@Column(name = "page_uid")
	private Long pageUid;

	@Setter
	@Column(name = "kihno_kit_uid")
	private Long kihnoKitUid;

	public Kit(String serialNumber, Long pageUid) {
		this.serialNumber = serialNumber;
		this.pageUid = pageUid;
	} 
}
