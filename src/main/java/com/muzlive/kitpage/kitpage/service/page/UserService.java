package com.muzlive.kitpage.kitpage.service.page;

import static com.muzlive.kitpage.kitpage.domain.page.QPage.page;
import static com.muzlive.kitpage.kitpage.domain.user.QInstallLog.installLog;
import static com.muzlive.kitpage.kitpage.domain.user.QKit.kit;
import static com.muzlive.kitpage.kitpage.domain.user.QKitLog.kitLog;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.user.InstallLog;
import com.muzlive.kitpage.kitpage.domain.user.Kit;
import com.muzlive.kitpage.kitpage.domain.user.KitLog;
import com.muzlive.kitpage.kitpage.domain.user.Member;
import com.muzlive.kitpage.kitpage.domain.user.MemberLog;
import com.muzlive.kitpage.kitpage.domain.user.QInstallLog;
import com.muzlive.kitpage.kitpage.domain.user.QKitLog;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.repository.InstallLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.KitRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.TokenLogRepository;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private final JPAQueryFactory queryFactory;

	private final KitRepository kitRepository;

	private final KitLogRepository kitLogRepository;

	private final MemberRepository memberRepository;

	private final MemberLogRepository memberLogRepository;

	private final TokenLogRepository tokenLogRepository;

	private final InstallLogRepository installLogRepository;

	@Override
	public UserDetails loadUserByUsername(String deviceId) throws UsernameNotFoundException {
		memberRepository.findByDeviceId(deviceId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NOT_FOUND));

		return null;
	}

	@Transactional
	public void insertTokenLog(TokenLog tokenLog) throws Exception {
		tokenLogRepository.save(tokenLog);
	}

	@Transactional
	public void insertInstallLog(InstallLog installLog) throws Exception {
		installLogRepository.save(installLog);
	}

	@Transactional
	public Member findByDeviceId(String deviceId) throws Exception {
		return memberRepository.findByDeviceIdWithLock(deviceId).orElse(Member.builder().build());
	}

	@Transactional
	public Member saveMemberAndLog(Member member) throws Exception {
		member = memberRepository.save(member);
		memberLogRepository.save(MemberLog.of(member));

		return member;
	}

	@Transactional
	public void upsertMemberLog(String deviceId, String modelName, String ipAddress) throws Exception {
		Member member = memberRepository.findByDeviceIdWithLock(deviceId).orElseGet(() -> Member.builder()
			.deviceId(deviceId)
			.build());
		member.setModelName(modelName);
		member.setIpAddress(ipAddress);

		this.saveMemberAndLog(member);
	}

	public Kit findBySerialNumber(String serialNumber) throws Exception {
		return kitRepository.findBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	public KitLog findLatestKitLog(String deviceId, String serialNumber) throws Exception {
		return kitLogRepository.findFirstByDeviceIdAndSerialNumberOrderByKitLogUidDesc(deviceId, serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
	}

	@Transactional
	public Kit checkTag(String serialNumber, Long kihnoKitUid) throws Exception {
		Kit kit = kitRepository.findBySerialNumber(serialNumber).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
		kit.setKihnoKitUid(kihnoKitUid);
		return this.upsertKit(kit);
	}

	@Transactional
	public Kit upsertKit(Kit kit) throws Exception {
		kit = kitRepository.save(kit);
		kitLogRepository.save(KitLog.of(kit));

		return kit;
	}

	@Transactional
	public void clearDeviceIdHistory(String deviceId) throws Exception {
		List<Kit> kits = kitRepository.findByDeviceId(deviceId).orElseThrow(() -> new CommonException(ExceptionCode.CANNOT_FIND_MATCHED_ITEM));
		kits.forEach(k -> k.setDeviceId(""));
	}

	public List<Tuple> getInstallLogs(String deviceId, String contentId, Region region) throws Exception {
		QInstallLog installLogSub = new QInstallLog("installLogSub");

		List<Tuple> tuples = queryFactory
			.select(installLog, kit)
			.from(installLog)
				.leftJoin(kit).on(kit.deviceId.eq(installLog.deviceId).and(kit.kitUid.eq(installLog.kitUid)))
			.where(installLog.deviceId.eq(deviceId)
				.and(installLog.installLogUid.in(
						JPAExpressions
						.select(installLogSub.installLogUid.max())
						.from(installLogSub)
						.innerJoin(page).on(page.pageUid.eq(installLogSub.pageUid))
						.where(installLogSub.deviceId.eq(deviceId)
							.and(page.contentId.eq(contentId).and(page.region.eq(region))))
						.groupBy(page.pageUid))))
			.fetch();

		if(CollectionUtils.isEmpty(tuples)) tuples = new ArrayList<>();

		return tuples;
	}

	public Page getPageBySerialNumber(String serialNumber) throws Exception {
		return queryFactory
			.selectFrom(page)
			.innerJoin(kit).on(kit.pageUid.eq(page.pageUid))
			.where(kit.serialNumber.eq(serialNumber))
			.fetchFirst();
	}

}
