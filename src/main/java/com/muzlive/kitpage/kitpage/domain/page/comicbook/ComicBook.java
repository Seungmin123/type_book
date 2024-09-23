package com.muzlive.kitpage.kitpage.domain.page.comicbook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.domain.page.Page;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "comic_book")
@Entity
public class ComicBook extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comic_book_uid", nullable = false)
	private Long comicBookUid;

	@Column(name = "writer")
	private String writer;

	@Column(name = "illustrator")
	private String illustrator;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "page_uid")
	private Page page;

	@OneToMany(mappedBy = "comicBook")
	private List<ComicBookDetail> comicBookDetails;

	@Builder
	public ComicBook(String writer, String illustrator, Page page) {
		this.writer = writer;
		this.illustrator = illustrator;
		this.page = page;
	}
}
