package com.muzlive.kitpage.kitpage.domain.user.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VersionInfoResp {


    private Boolean needUpdate;

    private Boolean isForced;


}