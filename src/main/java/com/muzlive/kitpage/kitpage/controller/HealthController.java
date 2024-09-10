package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/health")
@RestController
public class HealthController {

	@GetMapping("/")
	CommonResp<Void> healthCheck() throws Exception {
		return new CommonResp<>();
	}

}
