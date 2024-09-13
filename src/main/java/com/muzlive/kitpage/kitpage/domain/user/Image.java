package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import com.muzlive.kitpage.kitpage.utils.enums.ImageCode;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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

	@Column(name = "image_size")
	private Long imageSize;

	@Column(name = "width")
	private Integer width;

	@Column(name = "height")
	private Integer height;

	@Enumerated
	@Column(name = "image_code", nullable = false)
	private ImageCode imageCode;

	@Builder
	public Image(String imagePath, Long imageSize, Integer width, Integer height, ImageCode imageCode) {
		this.imagePath = imagePath;
		this.imageSize = imageSize;
		this.width = width;
		this.height = height;
		this.imageCode = imageCode;
	}

	public static Image of(String imagePath, ImageCode imageCode, MultipartFile multipartFile) {
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
			.build();
	}
}
