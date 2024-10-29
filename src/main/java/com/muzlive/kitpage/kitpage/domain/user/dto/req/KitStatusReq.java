package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KitStatusReq {

    @NotNull
    private String deviceId;

    @NotNull
    private String serialNumber;
}
