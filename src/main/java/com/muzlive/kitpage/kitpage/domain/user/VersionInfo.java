package com.muzlive.kitpage.kitpage.domain.user;

import com.muzlive.kitpage.kitpage.domain.common.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "version_info")
@Entity
public class VersionInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_info_uid", nullable = false)
    private Long versionInfoUid;

    @Column(name = "os_type")
    private String osType;

    @Setter
    @Column(name = "latest_version")
    private String latestVersion;

    @Column(name = "forced_version")
    private String forcedVersion;

    @Column(name = "os_version")
    private String osVersion;


}
