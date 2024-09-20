package com.muzlive.kitpage.kitpage.service.transfer.kihno.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KihnoMicLocationResp {

    @JsonProperty("model_id")
    private String modelName;

    @JsonProperty("mike_place")
    private String mikePlace;

    @JsonProperty("earphone_place")
    private String earphonePlace;

    @JsonProperty("mike_face_screen_length")
    private Float mikeFaceScreenLength;

    @JsonProperty("location_mike_top")
    private Float locationMikeTop;

    @JsonProperty("location_mike_left")
    private Float locationMikeLeft;

    @JsonProperty("earphone_face_screen_length")
    private Float earphoneFaceScreenLength;

    @JsonProperty("location_earphone_top")
    private Float locationEarphoneTop;

    @JsonProperty("location_earphone_left")
    private Float locationEarphoneLeft;

    @JsonProperty("_foldable")
    private boolean isFoldable;

    @JsonProperty("errorMSG")
    private String errorMsg;

    @JsonProperty("skin_info")
    private String skinInfo;

}
