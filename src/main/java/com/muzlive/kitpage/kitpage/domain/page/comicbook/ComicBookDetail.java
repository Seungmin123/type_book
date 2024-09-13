package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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

	@Column(name = "title")
	private String title;

	@Setter
	@Column(name = "chapter")
	private String chapter;

	@Setter
	@Column(name = "page")
	private Integer page;

	@Column(name = "rate")
	private Double rate;

	@Column(name = "imageUid")
	private Long imageUid;

	@Enumerated
	@Column(name = "region")
	private Region region;

	@ManyToOne
	@JoinColumn(name = "comic_book_uid", insertable = false, updatable = false)
	private ComicBook comicBook;

	@Builder
	public ComicBookDetail(Long comicBookUid, String title, String chapter,
		Double rate, Long imageUid, Region region) {
		this.comicBookUid = comicBookUid;
		this.title = title;
		this.chapter = chapter;
		this.rate = rate;
		this.imageUid = imageUid;
		this.region = region;
	}
}
