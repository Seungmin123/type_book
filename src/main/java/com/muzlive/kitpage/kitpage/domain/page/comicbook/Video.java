package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.VideoCode;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "video")
@Entity
public class Video extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_uid", nullable = false)
	private Long videoUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Column(name = "artist")
	private String artist;

	@Column(name = "title")
	private String title;

	@Column(name = "stream_url", nullable = false)
	private String streamUrl;

	@Setter
	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Enumerated(EnumType.STRING)
	@Column(name = "video_code")
	private VideoCode videCode;

	@Builder
	public Video(String contentId, String artist, String title, String streamUrl,
		Long coverImageUid, VideoCode videCode) {
		this.contentId = contentId;
		this.artist = artist;
		this.title = title;
		this.streamUrl = streamUrl;
		this.coverImageUid = coverImageUid;
		this.videCode = videCode;
	}
}
