package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
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

	@Column(name = "writer")
	private String writer;

	@Column(name = "illustrator")
	private String illustrator;

	@Column(name = "volume")
	private Integer volume;

	@Column(name = "volume_unit")
	private String volumeUnit;

	@Column(name = "page_unit")
	private String pageUnit;

	@Column(name = "genre")
	private String genre;

	@Transient
	private List<String> genreList;

	@ManyToOne
	@JoinColumn(name = "page_uid", insertable = false, updatable = false)
	private Page page;

	@OneToMany(mappedBy = "comicBook")
	@OrderBy("comicBookDetailUid ASC")
	private List<ComicBookDetail> comicBookDetails;

	@PrePersist
	@PreUpdate
	public void convertListToString() {
		if(genreList != null)
			this.genre = String.join(",", genreList);
	}

	@PostLoad
	public void convertStringToList() {
		if(genre != null && !genre.isEmpty())
			this.genreList = Arrays.asList(genre.split(","));
	}

	@Builder
	public ComicBook(Long pageUid, Long coverImageUid, String writer, String illustrator, Integer volume, String volumeUnit, String pageUnit) {
		this.pageUid = pageUid;
		this.coverImageUid = coverImageUid;
		this.writer = writer;
		this.illustrator = illustrator;
		this.volume = volume;
		this.volumeUnit = volumeUnit;
		this.pageUnit = pageUnit;
	}
}
