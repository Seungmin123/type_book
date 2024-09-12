package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.BookmarkStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
@Table(name = "comic_book_bookmark")
@Entity
public class ComicBookBookmark extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bookmark_uid", nullable = false)
	private Long bookmarkUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Column(name = "detail_uid", nullable = false)
	private Long detailUid;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "kit_uid", nullable = false)
	private Long kitUid;

	@Enumerated
	@Column(name = "status")
	private BookmarkStatus status;

}
