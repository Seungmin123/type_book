package com.muzlive.kitpage.kitpage.domain.page;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "content")
@Entity
public class Content extends BaseTimeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_uid", nullable = false)
	private Long contentUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private PageContentType contentType;

	@Column(name = "title")
	private String title;

	@Column(name = "info_text")
	private String infoText;

	@Setter
	@Column(name = "cover_image_uid")
	private Long coverImageUid;

	@Enumerated(EnumType.STRING)
	@Column(name = "region")
	private Region region;

	@OneToMany(mappedBy = "content", fetch = FetchType.EAGER)
	@OrderBy("pageUid ASC")
	private List<Page> pages;

	@Builder
	public Content(String contentId, PageContentType contentType, String title,
		String infoText, Long coverImageUid, Region region) {
		this.contentId = contentId;
		this.contentType = contentType;
		this.title = title;
		this.infoText = infoText;
		this.coverImageUid = coverImageUid;
		this.region = region;
	}
}
