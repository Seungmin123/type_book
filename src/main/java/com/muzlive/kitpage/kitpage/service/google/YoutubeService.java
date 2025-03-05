package com.muzlive.kitpage.kitpage.service.google;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.util.List;
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

	public String getYoutubeThumbnailUrl(List<com.google.api.services.youtube.model.Video> youtubeResp) {

		String url = null;

		if(youtubeResp.get(0).getSnippet().getThumbnails().getStandard() != null) {
			url = youtubeResp.get(0).getSnippet().getThumbnails().getStandard().getUrl();
		} else if(youtubeResp.get(0).getSnippet().getThumbnails().getHigh() != null) {
			url = youtubeResp.get(0).getSnippet().getThumbnails().getHigh().getUrl();
		} else if(youtubeResp.get(0).getSnippet().getThumbnails().getMedium() != null) {
			url = youtubeResp.get(0).getSnippet().getThumbnails().getMedium().getUrl();
		} else {
			url = youtubeResp.get(0).getSnippet().getThumbnails().getDefault().getUrl();
		}

		return url;
	}

}
