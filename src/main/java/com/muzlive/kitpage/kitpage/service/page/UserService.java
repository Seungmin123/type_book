package com.muzlive.kitpage.kitpage.service.page;

import com.muzlive.kitpage.kitpage.config.exception.CommonException;
import com.muzlive.kitpage.kitpage.config.exception.ExceptionCode;
import com.muzlive.kitpage.kitpage.domain.user.Member;
import com.muzlive.kitpage.kitpage.domain.user.MemberLog;
import com.muzlive.kitpage.kitpage.domain.user.TokenLog;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberLogRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.MemberRepository;
import com.muzlive.kitpage.kitpage.domain.user.repository.TokenLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private final MemberRepository memberRepository;

	private final MemberLogRepository memberLogRepository;

	private final TokenLogRepository tokenLogRepository;

	@Override
	public UserDetails loadUserByUsername(String deviceId) throws UsernameNotFoundException {
		memberRepository.findByDeviceId(deviceId).orElseThrow(() -> new CommonException(ExceptionCode.USER_NOT_FOUND));

		return null;
	}

	@Transactional
	public void insertTokenLog(TokenLog tokenLog) throws Exception {
		tokenLogRepository.save(tokenLog);
	}

	public Member findByDeviceId(String deviceId) throws Exception {
		return memberRepository.findByDeviceId(deviceId).orElse(Member.builder().build());
	}

	@Transactional
	public Member upsertMember(Member member) throws Exception {
		member = memberRepository.save(member);
		memberLogRepository.save(MemberLog.of(member));

		return member;
	}
}
