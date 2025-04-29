package com.muzlive.kitpage.kitpage.domain.page.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.muzlive.kitpage.kitpage.config.serializer.LocalDateTimeSerializer;
import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommonEpisodeResp {

	private Long coverImageUid;

	private String coverImagePath;

	private Integer pageSize;

	private ReadingDirection readingDirection;

	private List<? extends CommonEpisodeDetailResp> detailPages;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastModifiedAt;

	public CommonEpisodeResp(Long coverImageUid, String coverImagePath, ReadingDirection readingDirection) {
		this.coverImageUid = coverImageUid;
		this.coverImagePath = coverImagePath;
		this.readingDirection = readingDirection;
	}

	public CommonEpisodeResp(List<? extends CommonEpisodeDetailResp> detailPages, Integer pageSize, LocalDateTime lastModifiedAt) {
		this.detailPages = detailPages;
		this.pageSize = pageSize;
		this.lastModifiedAt = lastModifiedAt;
	}
}
