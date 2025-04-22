package com.muzlive.kitpage.kitpage.domain.page.photobook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBookDetail;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "photo_book")
@Entity
public class PhotoBook extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "photo_book_uid", nullable = false)
	private Long photoBookUid;

	@Column(name = "page_uid", nullable = false)
	private Long pageUid;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Column(name = "title")
	private String title;

	@ManyToOne
	@JoinColumn(name = "page_uid", insertable = false, updatable = false)
	private Page page;

	@OneToMany(mappedBy = "photoBook")
	@OrderBy("page ASC")
	private List<PhotoBookDetail> photoBookDetails;

	@Builder
	public PhotoBook(Long pageUid, Long coverImageUid, String title) {
		this.pageUid = pageUid;
		this.coverImageUid = coverImageUid;
		this.title = title;
	}
}
