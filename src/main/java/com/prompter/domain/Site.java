package com.prompter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "site")
public class Site {

    @Id
    @Column(name = "site_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "content")
    private String content;

    @Column(name = "is_ads")
    private Boolean isAds;

    public static Site createInitSite(String url, String content, Boolean isAds) {
        return new Site(url, content, isAds);
    }

    public static Site createAfterSite(String content, Boolean isAds) {
        return new Site(content, isAds);
    }

    private Site(String content, Boolean isAds) {
        this.content = content;
        this.isAds = isAds;
    }

    private Site(String url, String content, Boolean isAds) {
        this.url = url;
        this.content = content;
        this.isAds = isAds;
    }

    public void updateText(String content) {
        this.content = content;
    }

    public void updateIsAds(Boolean isAds) {
        this.isAds = isAds;
    }
}
