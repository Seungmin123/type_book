package com.muzlive.kitpage.kitpage.domain.page;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.page.photobook.PhotoBook;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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

	@Column(name = "content_id", nullable = false, length = 100)
	private String contentId;

	@Column(name = "album_id")
	private String albumId;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Column(name = "title")
	private String title;

	@Column(name = "subtitle")
	private String subTitle;

	@Column(name = "info_text")
	private String infoText;

	@Column(name = "rate")
	private Double rate;

	@OneToMany(mappedBy = "page", fetch = FetchType.LAZY)
	@OrderBy("comicBookUid ASC")
	private List<ComicBook> comicBooks;

	@OneToMany(mappedBy = "page", fetch = FetchType.LAZY)
	@OrderBy("photoBookUid ASC")
	private List<PhotoBook> photoBooks;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id", referencedColumnName = "content_id", insertable = false, updatable = false)
	private Content content;

	@Builder
	public Page(String contentId, String albumId, Long coverImageUid,
		Double rate, String title, String subTitle, String infoText) {
		this.contentId = contentId;
		this.albumId = albumId;
		this.coverImageUid = coverImageUid;
		this.title = title;
		this.subTitle = subTitle;
		this.infoText = infoText;
		this.rate = rate;
	}
}
