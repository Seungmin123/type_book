package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsCommonListResp<T> {

	@JsonProperty("total_count")
	private Integer totalCount;

	@JsonProperty("row_count")
	private Integer rowCount;

	@JsonProperty("file_download_url")
	private String fileDownloadUrl;

	@JsonProperty("is_result")
	private Boolean isResult;

	@JsonProperty("no_read_count")
	private Integer noReadCount;

	@JsonProperty("list")
	private List<T> list;

	@JsonProperty("list2")
	private Object list2;

	@JsonProperty("is_folder")
	private Boolean isFolder;

	@JsonProperty("is_all_view")
	private Boolean isAllView;

}
