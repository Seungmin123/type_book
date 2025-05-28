package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyKitResp {

	private String imagePath;

	private Long pageUid;

	private String title;

	private String writer;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastTaggedAt;
}
