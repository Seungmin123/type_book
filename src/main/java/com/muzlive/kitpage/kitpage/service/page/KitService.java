package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KitService {

	private final JwtTokenProvider jwtTokenProvider;

	private final KitRepository kitRepository;

	private final KitLogRepository kitLogRepository;

	@Transactional
	public Kit checkTag(String deviceId, String serialNumber, Long kihnoKitUid) throws Exception {
		Kit kit = kitRepository.findBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));

		kit.setDeviceId(deviceId);
		kit.setKihnoKitUid(kihnoKitUid);

		kitRepository.save(kit);
		kitLogRepository.save(KitLog.of(kit));

		return kit;
	}

}
