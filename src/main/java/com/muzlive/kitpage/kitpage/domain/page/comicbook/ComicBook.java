package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.ReadingDirection;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "comic_book")
@Entity
public class ComicBook extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comic_book_uid", nullable = false)
	private Long comicBookUid;

	@Column(name = "page_uid", nullable = false)
	private Long pageUid;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Column(name = "volume")
	private Integer volume;

	@ManyToOne
	@JoinColumn(name = "page_uid", insertable = false, updatable = false)
	private Page page;

	@OneToMany(mappedBy = "comicBook")
	@OrderBy("page ASC")
	private List<ComicBookDetail> comicBookDetails;

	@Builder
	public ComicBook(Long pageUid, Long coverImageUid, Integer volume) {
		this.pageUid = pageUid;
		this.coverImageUid = coverImageUid;
		this.volume = volume;
	}
}
