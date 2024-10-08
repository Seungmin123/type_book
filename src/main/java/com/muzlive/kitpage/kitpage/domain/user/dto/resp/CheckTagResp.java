package com.muzlive.kitpage.kitpage.domain.user.dto.resp;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTagResp<T> {

	private String token;

	private List<T> list;

	private T tagged;

}
