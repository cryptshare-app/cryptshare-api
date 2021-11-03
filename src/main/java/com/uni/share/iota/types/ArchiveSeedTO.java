package com.uni.share.iota.types;

import java.time.LocalDateTime;
import java.util.Date;

public class ArchiveSeedTO {
    String seed;
    LocalDateTime createdAt;

    public ArchiveSeedTO() {
    }

    public ArchiveSeedTO(String seed, LocalDateTime createdAt) {
        this.seed = seed;
        this.createdAt = createdAt;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
