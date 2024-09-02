package com.example.springauth.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class SoftDeletable extends Auditable {

    @Column(name = "delete_date")
    private LocalDateTime deletedAt;
}
