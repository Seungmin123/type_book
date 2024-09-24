package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "comic_book_detail")
@Entity
public class ComicBookDetail extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comic_book_detail_uid", nullable = false)
	private Long comicBookDetailUid;

	@Column(name = "comic_book_uid", nullable = false)
	private Long comicBookUid;

	@Column(name = "episode")
	private String episode;

	@Setter
	@Column(name = "page")
	private Integer page;

	@Column(name = "rate")
	private Double rate;

	@Column(name = "image_uid")
	private Long imageUid;

	@ManyToOne
	@JoinColumn(name = "comic_book_uid", insertable = false, updatable = false)
	private ComicBook comicBook;

	@Builder
	public ComicBookDetail(Long comicBookUid, String episode, Integer page,
		Double rate, Long imageUid) {
		this.comicBookUid = comicBookUid;
		this.episode = episode;
		this.page = page;
		this.rate = rate;
		this.imageUid = imageUid;
	}
}
