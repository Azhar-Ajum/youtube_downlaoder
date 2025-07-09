package com.yt.down;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DownloadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String instagramUrl;
    private String downloadedFile;
    private LocalDateTime downloadTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public String getDownloadedFile() { return downloadedFile; }
    public void setDownloadedFile(String downloadedFile) { this.downloadedFile = downloadedFile; }

    public LocalDateTime getDownloadTime() { return downloadTime; }
    public void setDownloadTime(LocalDateTime downloadTime) { this.downloadTime = downloadTime; }
}
