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
@Table(name = "comic_book_log")
@Entity
public class ComicBookLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comic_book_log_uid", nullable = false)
	private Long comicBookLogUid;

	@Column(name = "kit_uid", nullable = false)
	private Long kitUid;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "comic_book_detail_uid", nullable = false)
	private Long comicBookDetailUid;
}
