package com.study.datajpa.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @PrePersist // 영속전 발생하는 이벤트
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate // 업데이트 전 발생 이벤트
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
