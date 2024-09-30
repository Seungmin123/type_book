package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.TokenType;
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
@Table(name = "token_log")
@Entity
public class TokenLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_log_uid", nullable = false)
	private Long tokenLogUid;

	@Column(name = "token")
	private String token;

	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private TokenType type;

	@Builder
	public TokenLog(String token, String deviceId, String serialNumber, String email, TokenType tokenType) {
		this.token = token;
		this.deviceId = deviceId;
		this.serialNumber = serialNumber;
		this.email = email;
		this.type = tokenType;
	}

}
