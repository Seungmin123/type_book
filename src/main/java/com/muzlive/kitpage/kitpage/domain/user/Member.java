package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import com.muzlive.kitpage.kitpage.utils.enums.UserRole;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@Enumerated
	@Column(name = "role")
	private UserRole role;

	@Column(name = "nickname")
	private String nickName;

	@Column(name = "profile_image_uid")
	private Long profileImageUid;

	@Column(name = "ip_address")
	private String ipAddress;

	@Enumerated
	@Column(name = "region")
	private Region region;

	@Column(name = "model_name")
	private String modelName;

}
