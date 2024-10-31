package com.muzlive.kitpage.kitpage.domain.page;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "content")
@Entity
public class Content extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_uid", nullable = false)
	private Long contentUid;

	@Column(name = "content_id", nullable = false)
	private String contentId;

	@Column(name = "info_text")
	private String infoText;

	@Column(name = "cover_image_uid")
	private Long coverImageUid;

}
