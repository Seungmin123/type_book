package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "member_log")
@Entity
public class MemberLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_log_uid", nullable = false)
	private Long memberLogUid;

	@Column(name = "member_uid", nullable = false)
	private Long memberUid;

	@Column(name = "ip_address")
	private String ipAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Region region;

	@Column(name = "model_name")
	private String modelName;

	@Builder
	public MemberLog(Long memberUid, String ipAddress, Region region, String modelName) {
		this.memberUid = memberUid;
		this.ipAddress = ipAddress;
		this.region = region;
		this.modelName = modelName;
	}

	public static MemberLog of(Member member) {
		return MemberLog.builder()
			.memberUid(member.getMemberUid())
			.ipAddress(member.getIpAddress())
			.region(member.getRegion())
			.modelName(member.getModelName())
			.build();
	}
}
