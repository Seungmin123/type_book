package com.muzlive.kitpage.kitpage.domain.page.photobook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.comicbook.ComicBook;
import com.muzlive.kitpage.kitpage.domain.user.Image;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "photo_book_detail")
@Entity
public class PhotoBookDetail extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "photo_book_detail_uid", nullable = false)
	private Long photoBookDetailUid;

	@Column(name = "photo_book_uid", nullable = false)
	private Long photoBookUid;

	@Setter
	@Column(name = "page")
	private Integer page;

	@Column(name = "image_uid")
	private Long imageUid;

	@Column(name = "pdf_uid")
	private Long pdfUid;

	@OneToOne
	@JoinColumn(name = "image_uid", insertable = false, updatable = false)
	private Image image;

	@OneToOne
	@JoinColumn(name = "pdf_uid", insertable = false, updatable = false)
	private Pdf pdf;

	@ManyToOne
	@JoinColumn(name = "photo_book_uid", insertable = false, updatable = false)
	private PhotoBook photoBook;

	@Builder
	public PhotoBookDetail(Long photoBookUid, Integer page, Long imageUid, Long pdfUid) {
		this.photoBookUid = photoBookUid;
		this.page = page;
		this.imageUid = imageUid;
		this.pdfUid = pdfUid;
	}
}
