package com.muzlive.kitpage.kitpage.config.ffmpeg;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
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

	@Bean
	public FFmpeg ffmpeg() throws IOException {
		return new FFmpeg(path);
	}

	@Bean
	public FFprobe ffprobe() throws IOException {
		return new FFprobe(probe);
	}

	@Bean
	public FFmpegExecutor ffmpegExecutor(FFmpeg ffmpeg, FFprobe ffprobe) throws IOException {
		return new FFmpegExecutor(ffmpeg, ffprobe);
	}
}
