package com.muzlive.kitpage.kitpage.utils;

import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

	public String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	public static String makeEpisodeName(Integer volume, String episode) {
		String name = "";
		if(volume != null) name += volume + " 권";
		if(episode != null) name += " " + episode;

		return name;
	}

	public byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

	public Region getCustomRegion(Locale locale) {
		switch (locale.getLanguage()) {
			case "ko":
			case "kr":
				return Region.KOR;
			case "jp":
			case "ja":
				return Region.JPN;
			default:
				return Region.ENG;
		}
	}

	public String convertDurationToString(Duration duration) throws Exception {
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();

		StringBuilder formattedDuration = new StringBuilder();
		if (hours > 0) {
			formattedDuration.append(hours).append(":");
		}

		// 분이 없는 경우 00으로 표시, 한 자리일 경우 앞에 0 추가
		formattedDuration.append(String.format("%02d:", minutes));

		// 초는 한 자리일 경우 앞에 0 추가
		formattedDuration.append(String.format("%02d", seconds));

		return formattedDuration.toString();
	}

	public String base64Decode(final String str){
		if(StringUtils.isEmpty(str)) return "";
		return new String(Base64.getDecoder().decode(str));
	}

}
