package com.muzlive.kitpage.kitpage.domain.user.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeToLocalDateSerializer;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KitStatusResp {

	private String csId;

	private String title;

	private String subTitle;

	private Long coverImageUid;

	private Boolean isInstalled = Boolean.FALSE;

	@JsonSerialize(using = LocalDateTimeToLocalDateSerializer.class)
	private LocalDateTime createdAt;
}
