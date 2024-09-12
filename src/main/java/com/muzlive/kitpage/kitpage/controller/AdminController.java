package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.req.UploadComicBookReq;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@RestController
public class AdminController {

	// TODO Upload Comic Book Info

	// TODO Upload Comic Book Detail

	// TODO Upload Music

	// TODO Upload Video

	@PostMapping("/comic")
	CommonResp<Void> uploadComicBook(@Valid UploadComicBookReq uploadComicBookReq) throws Exception {

		return new CommonResp<>();
	}
}
