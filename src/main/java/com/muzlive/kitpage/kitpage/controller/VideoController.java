package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.SnsTransferService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoDetailReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.SnsVideoVttReq;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "Video API")
@RequestMapping("/v1/video")
@RestController
public class VideoController {

	@Value("${spring.page.domain}")
	private String pageDomain;

	private final SnsTransferService snsTransferService;

	@Operation(summary = "Bitmovin M3U8 마스터 플레이리스트 URL 조회 API")
	@GetMapping
	public CommonResp<String> getVideo(@ModelAttribute SnsVideoReq snsVideoReq) throws Exception {
		return new CommonResp<>(snsTransferService.fetchVideoStreamUrl(snsVideoReq));
	}

	@Hidden
	@GetMapping("/public/detail")
	public ResponseEntity<InputStreamResource> getVideoDetail(@ModelAttribute SnsVideoDetailReq snsVideoDetailReq) throws Exception {
		return snsTransferService.generateM3U8Response(snsTransferService.fetchVideoDetail(snsVideoDetailReq));
	}

	@Hidden
	@GetMapping("/public/vtt")
	public ResponseEntity<InputStreamResource> getVideoVtt(@ModelAttribute SnsVideoVttReq snsVideoVttReq) throws Exception {
		return snsTransferService.generateM3U8Response(snsTransferService.fetchVideoVtt(snsVideoVttReq));
	}

}
