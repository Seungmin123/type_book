package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_uid", nullable = false)
	private Long memberUid;

	@Setter
	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@Column(name = "region")
	private Region region;

	@Column(name = "nickname")
	private String nickName;

	@Column(name = "profile_image_uid")
	private Long profileImageUid;

	@Setter
	@Column(name = "ip_address")
	private String ipAddress;

	@Setter
	@Column(name = "model_name")
	private String modelName;

	@Setter
	@Column(name = "kittor_token")
	private String kittorToken;

	@Setter
	@Column(name = "kittor_refresh_token")
	private String kittorRefreshToken;

	@Builder
	public Member(String deviceId, String email, String password, String name, Region region,
		String nickName, Long profileImageUid, String ipAddress, String modelName, String kittorToken, String kittorRefreshToken) {
		this.deviceId = deviceId;
		this.email = email;
		this.password = password;
		this.name = name;
		this.region = region;
		this.nickName = nickName;
		this.profileImageUid = profileImageUid;
		this.ipAddress = ipAddress;
		this.modelName = modelName;
		this.kittorToken = kittorToken;
		this.kittorRefreshToken = kittorRefreshToken;
	}

}
