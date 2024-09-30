package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
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
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "member")
@Entity
@Builder
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

}
