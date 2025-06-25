package com.muzlive.kitpage.kitpage.domain.page.comicbook.dto.resp;

import com.muzlive.kitpage.kitpage.domain.page.comicbook.Video;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoResp {

	private String title;

	private String duration;

	private String streamUrl;

	// TODO 삭제 필요
	private Long coverImageUid;

	private String coverImagePath;

	private VideoCode videoCode;

	private String videoId;

	public VideoResp(Video video){
		this.title = video.getTitle();
		this.duration = video.getDuration();
		this.streamUrl = video.getStreamUrl();
		this.coverImageUid = video.getCoverImageUid();
		this.coverImagePath = video.getImage().getImagePath();
		this.videoCode = video.getVideoCode();
		this.videoId = video.getVideoId();
	}

}
