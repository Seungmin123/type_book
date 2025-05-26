package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorPreSignedUrlResp {

	private String fileUrl;

	private String fileName;

	private String uploadUrl;

	private String resultUrl;

}
