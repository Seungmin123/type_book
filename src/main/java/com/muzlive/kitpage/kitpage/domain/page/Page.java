package com.muzlive.kitpage.kitpage.domain.page;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.PageContentType;
import com.muzlive.kitpage.kitpage.utils.enums.PageGenre;
import com.muzlive.kitpage.kitpage.utils.enums.Region;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

	@Column(name = "contents_uid", nullable = false)
	private Long contentsUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Enumerated
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

	@Enumerated
	@Column(name = "genre")
	private PageGenre genre;

	@Column(name = "rate")
	private Double rate;

	@Enumerated
	@Column(name = "region")
	private Region region;

}
