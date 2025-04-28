package com.muzlive.kitpage.kitpage.domain.page.photobook;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "pdf")
@Entity
public class Pdf extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pdf_uid", nullable = false)
	private Long pdfUid;

	@Setter
	@Column(name = "pdf_path", nullable = false)
	private String pdfPath;

	@Setter
	@Column(name = "pdf_size")
	private Long pdfSize;

	@Setter
	@Column(name = "width")
	private Integer width;

	@Setter
	@Column(name = "height")
	private Integer height;

	@Column(name = "save_file_name", nullable = false)
	private String saveFileName;

	@Column(name = "original_file_name", nullable = false)
	private String originalFileName;

	@Builder
	public Pdf(String pdfPath, Long pdfSize, Integer width, Integer height,
		String saveFileName, String originalFileName) {
		this.pdfPath = pdfPath;
		this.pdfSize = pdfSize;
		this.width = width;
		this.height = height;
		this.saveFileName = saveFileName;
		this.originalFileName = originalFileName;
	}
}
