package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
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
@Table(name = "image")
@Entity
public class Image extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_uid", nullable = false)
	private Long imageUid;

	@Column(name = "image_path", nullable = false)
	private String imagePath;

	@Column(name = "image_size")
	private Long imageSize;

	@Column(name = "width")
	private Integer width;

	@Column(name = "height")
	private Integer height;

	@Enumerated
	@Column(name = "image_code", nullable = false)
	private ImageCode imageCode;

}
