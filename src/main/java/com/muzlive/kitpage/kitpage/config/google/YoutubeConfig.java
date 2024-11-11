package com.muzlive.kitpage.kitpage.config.google;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.google.youtube")
public class YoutubeConfig {

	private String name;

	private String key;

	@Bean
	public YouTube youtube() {
		return new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {})
			.setYouTubeRequestInitializer(new YouTubeRequestInitializer(key))
			.setApplicationName(name)
			.build();
	}

}
