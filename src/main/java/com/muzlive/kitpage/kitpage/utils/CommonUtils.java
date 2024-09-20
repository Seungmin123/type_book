package com.muzlive.kitpage.kitpage.utils;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommonUtils {

	public String makeRandomHexString() {
		Random rand = new Random();

		int randomNum = rand.nextInt(256); // 0 ~ 255 사이의 랜덤 숫자 생성

		return String.format("%02X", randomNum);
	}

}
