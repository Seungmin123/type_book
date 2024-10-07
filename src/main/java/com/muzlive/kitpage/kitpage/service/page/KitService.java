package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;
import static com.muzlive.kitpage.kitpage.domain.user.QKitLog.kitLog;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.config.jwt.JwtTokenProvider;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.domain.user.QKitLog;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KitService {

	private final JPAQueryFactory queryFactory;

	private final KitRepository kitRepository;

	private final KitLogRepository kitLogRepository;

	@Transactional
	public Kit checkTag(String deviceId, String serialNumber, Long kihnoKitUid) throws Exception {
		Kit kit = kitRepository.findBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
		kit.setDeviceId(deviceId);
		kit.setKihnoKitUid(kihnoKitUid);

		return this.upsertKit(kit);
	}

	@Transactional
	public Kit upsertKit(Kit kit) throws Exception {
		kit = kitRepository.save(kit);
		kitLogRepository.save(KitLog.of(kit));

		return kit;
	}

	public List<KitLog> getInstalledStatus(String contentId, String deviceId) throws Exception {
		QKitLog kitLogSub = new QKitLog("kitLogSub");

		List<KitLog> kitLogs = queryFactory
			.selectFrom(kitLog)
			.where(kitLog.kitLogUid.in(
				JPAExpressions
					.select(kitLogSub.kitLogUid.max())
					.from(kitLogSub)
					.innerJoin(page).on(page.pageUid.eq(kitLogSub.pageUid))
					.where(kitLogSub.deviceId.eq(deviceId)
						.and(page.contentId.eq(contentId)))))
			.fetch();

		return kitLogs;
	}

}
