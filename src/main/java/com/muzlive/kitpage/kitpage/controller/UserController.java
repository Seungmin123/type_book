package com.muzlive.kitpage.kitpage.controller;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.CheckTagReq;
import com.muzlive.kitpage.kitpage.domain.user.dto.req.MicLocationReq;
import com.muzlive.kitpage.kitpage.service.page.KitService;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.KihnoV2TransferSerivce;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoKitCheckReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicLocationReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.req.KihnoMicProcessedReq;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoKitCheckResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicLocationResp;
import com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp.KihnoMicProcessedResp;
import com.muzlive.kitpage.kitpage.utils.CommonUtils;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/user")
@RestController
public class UserController {

	private final KihnoV2TransferSerivce kihnoV2TransferSerivce;

	private final KitService kitService;

	private final CommonUtils commonUtils;

	@PostMapping("/checkTag")
	public CommonResp<Void> connect(@Valid @RequestBody CheckTagReq checkTagReq) throws Exception {
		String requestSerialNumber = (checkTagReq.getSerialNumber().length() > 8) ? checkTagReq.getSerialNumber().substring(0, 8) : checkTagReq.getSerialNumber();
		String paramSerialNumber = (checkTagReq.getSerialNumber().length() < 10) ? checkTagReq.getSerialNumber() + commonUtils.makeRandomHexString() : checkTagReq.getSerialNumber();
		
		KihnoKitCheckReq kihnoKitCheckReq = KihnoKitCheckReq.builder()
			.deviceId(checkTagReq.getDeviceId())
			.kitId(paramSerialNumber)
			.countryCode(checkTagReq.getCountryCode())
			.build();

		KihnoKitCheckResp kihnoKitCheckResp = kihnoV2TransferSerivce.kihnoKitCheck(kihnoKitCheckReq);

		Kit kit = kitService.checkTag(checkTagReq.getDeviceId(), requestSerialNumber, kihnoKitCheckResp.getKihnoKitUid());

		// TODO 추가 정보 더하여 Response 만들기 Install 이력이 있는 컨텐츠를 전부 보여줘야하는건지 코믹북만 보여줘야하는건지?



		return new CommonResp<>();
	}

	@GetMapping("/mic/processed")
	public CommonResp<KihnoMicProcessedResp> checkMicProcessed(@ModelAttribute @Valid MicLocationReq micLocationReq) throws Exception {
		return new CommonResp<>(kihnoV2TransferSerivce.checkMicProcessed(new KihnoMicProcessedReq(micLocationReq)));
	}

	@GetMapping("/mic")
	public CommonResp<KihnoMicLocationResp> getMicLocation(@ModelAttribute @Valid MicLocationReq micLocationReq) throws Exception {
		return new CommonResp<>(kihnoV2TransferSerivce.getMicLocation(new KihnoMicLocationReq(micLocationReq)));
	}

	// TODO Log in API

	// TODO Register API

	// TODO Get User Info API
}
