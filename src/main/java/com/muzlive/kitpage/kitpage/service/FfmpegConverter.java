package com.muzlive.kitpage.kitpage.service;

import com.muzlive.kitpage.kitpage.config.ffmpeg.FfmpegProperties;
import java.io.File;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FfmpegConverter {

	private final FfmpegProperties properties;

	public byte[] convertToWebp(byte[] imageBytes, String extension) throws Exception {
		FFmpeg ffmpeg = new FFmpeg(properties.getPath());
		FFprobe ffprobe = new FFprobe(properties.getProbe());

		File inputFile = File.createTempFile("input-", "." + extension);
		File outputFile = File.createTempFile("converted-", ".webp");

		Files.write(inputFile.toPath(), imageBytes);

		// 해상도 측정
		FFmpegProbeResult probeResult = ffprobe.probe(inputFile.getAbsolutePath());

		String scaleFilter = "scale='if(gt(iw\\,5120)\\,5120\\,iw)':'if(gt(iw\\,5120)\\,trunc(ih*5120/iw)\\,ih)'";

		// 빌더 구성
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(inputFile.getAbsolutePath())
			.overrideOutputFiles(true)
			.addOutput(outputFile.getAbsolutePath())
			.setFormat("webp")
			.setVideoCodec("libwebp")
			.addExtraArgs("-quality", "85")
			.addExtraArgs("-compression_level", "6")
			.addExtraArgs("-vf", scaleFilter)
			.done();

		new FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run();

		byte[] webpBytes = Files.readAllBytes(outputFile.toPath());

		// 임시 파일 정리
		inputFile.delete();
		outputFile.delete();

		return webpBytes;
	}

	// Optional: MultipartFile 처리 버전도 바로 가능
	public byte[] convertToWebp(MultipartFile multipartFile) throws Exception {
		String extension = getExtension(multipartFile.getOriginalFilename());
		return convertToWebp(multipartFile.getBytes(), extension);
	}

	private String getExtension(String filename) {
		if (filename == null || !filename.contains(".")) return "jpg";
		return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
	}

}
