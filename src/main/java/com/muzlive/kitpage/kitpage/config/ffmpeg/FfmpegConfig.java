package com.muzlive.kitpage.kitpage.config.ffmpeg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "ffmpeg")
public class FfmpegConfig {

	private String path;

	private String probe;

	@Bean(name = "ffmpegProperties")
	public FfmpegProperties ffmpegProperties() {
		return new FfmpegProperties(path, probe);
	}

}
