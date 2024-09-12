package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "music")
@Entity
public class Music extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "music_uid", nullable = false)
	private Long musicUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Column(name = "album")
	private String album;

	@Column(name = "artist")
	private String artist;

	@Column(name = "title")
	private String title;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	@Column(name = "play_time")
	private String playTime;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;
}
