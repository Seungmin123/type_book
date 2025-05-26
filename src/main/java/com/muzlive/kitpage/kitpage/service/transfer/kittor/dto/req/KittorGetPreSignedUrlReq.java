package com.muzlive.kitpage.kitpage.service.transfer.kittor.dto.req;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KittorGetPreSignedUrlReq {

	private List<KittorPreSginedFileReq> files;

}
