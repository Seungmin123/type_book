package com.muzlive.kitpage.kitpage.domain.user.dto.req;

import com.muzlive.kitpage.kitpage.utils.constants.ApplicationConstants;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionInfoReq {

    @NotNull
    private String currentVersion;

    @NotNull
    private String platform;

    @NotNull
    private String osVersion;

    private int compareTo(String version, String type) {
        if(version == null) return 1;

        String partSource;
        switch(type) {
            case "osVersion":
                partSource = this.osVersion;
                break;
            case "currentVersion":
                partSource = this.currentVersion;
                break;
            default: return 1;
        }

        String[] thisParts = partSource.split("\\.");
        String[] thatParts = version.split("\\.");

        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;

            if(thisPart < thatPart) return -1;
            if(thisPart > thatPart) return 1;
        }

        return 0;
    }

    public Boolean isCurrentVersionLessThanTo(String version) {
        return this.compareTo(version, ApplicationConstants.VERSION_TYPE_CURRENT) < 0;
    }

    public Boolean isOsVersionGreaterThanTo(String version) {
        return this.compareTo(version, ApplicationConstants.VERSION_TYPE_OS) >= 0;
    }

}
