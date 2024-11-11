package com.muzlive.kitpage.kitpage.service.google;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class YoutubeService {

	private final YouTube youtube;

	public List<Video> getVideoDetail(String videoId) throws IOException {
		YouTube.Videos.List request = youtube.videos()
			.list("snippet,contentDetails")
			.setId(videoId);

		VideoListResponse response = request.execute();
		return response.getItems();
	}

	public List<Video> getMultipleVideoDetails(List<String> videoIds) throws IOException {
		String videoIdsParam = String.join(",", videoIds);

		YouTube.Videos.List request = youtube.videos()
			.list("snippet,contentDetails")
			.setId(videoIdsParam);

		VideoListResponse response = request.execute();
		return response.getItems();
	}

}
