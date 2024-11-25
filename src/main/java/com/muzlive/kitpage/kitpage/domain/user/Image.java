package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.multipart.MultipartFile;

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

	@Setter
	@Column(name = "image_path", nullable = false)
	private String imagePath;

	@Setter
	@Column(name = "image_size")
	private Long imageSize;

	@Setter
	@Column(name = "width")
	private Integer width;

	@Setter
	@Column(name = "height")
	private Integer height;

	@Column(name = "original_file_name", nullable = false)
	private String originalFileName;

	@Setter
	@Column(name = "save_file_name", nullable = false)
	private String saveFileName;

	@Enumerated(EnumType.STRING)
	@Column(name = "image_code", nullable = false)
	private ImageCode imageCode;

	@Setter
	@Column(name = "md5", nullable = false)
	private String md5;

	@Builder
	public Image(String imagePath, Long imageSize, Integer width, Integer height, String originalFileName, String saveFileName, ImageCode imageCode, String md5) {
		this.imagePath = imagePath;
		this.imageSize = imageSize;
		this.width = width;
		this.height = height;
		this.originalFileName = originalFileName;
		this.saveFileName = saveFileName;
		this.imageCode = imageCode;
		this.md5 = md5;
	}

	public static Image of(String imagePath, ImageCode imageCode, MultipartFile multipartFile) throws IOException {
		int width = 0;
		int height = 0;

		try{
			BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
			width = bufferedImage.getWidth();
			height = bufferedImage.getWidth();
		}catch (Exception ignore){}

		return Image.builder()
			.imagePath(imagePath)
			.imageCode(imageCode)
			.imageSize(multipartFile.getSize())
			.width(width)
			.height(height)
			.originalFileName(multipartFile.getOriginalFilename())
			.md5(DigestUtils.md5Hex(multipartFile.getBytes()))
			.build();
	}
}
