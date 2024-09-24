package com.muzlive.kitpage.kitpage.domain.page;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "page")
@Entity
public class Page extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "page_uid", nullable = false)
	private Long pageUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private PageContentType contentType;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Column(name = "title")
	private String title;

	@Column(name = "subtitle")
	private String subTitle;

	@Column(name = "info_text")
	private String infoText;

	@Column(name = "company")
	private String company;

	@Enumerated(EnumType.STRING)
	@Column(name = "genre")
	private PageGenre genre;

	@Column(name = "rate")
	private Double rate;

	@Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Region region;

	@OneToMany(mappedBy = "page")
	private List<ComicBook> comicBooks;

	@Builder
	public Page(String contentId, PageContentType contentType,
		Long coverImageUid, String title, String subTitle, String infoText, String company,
		PageGenre genre, Double rate, Region region) {
		this.contentId = contentId;
		this.contentType = contentType;
		this.coverImageUid = coverImageUid;
		this.title = title;
		this.subTitle = subTitle;
		this.infoText = infoText;
		this.company = company;
		this.genre = genre;
		this.rate = rate;
		this.region = region;
	}
}
