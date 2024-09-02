package com.example.springauth.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<U> implements Auditable<U, Long, LocalDateTime> {

    @CreatedBy
    @Column(name = "creator", nullable = false, updatable = false)
    private U createdBy;

    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "updater")
    private U updatedBy;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updatedDate;

    @Override
    @NonNull
    public Optional<U> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(@NonNull U createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    @NonNull
    public Optional<LocalDateTime> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @Override
    public void setCreatedDate(@NonNull LocalDateTime creationDate) {
        this.createdDate = creationDate;
    }

    @NonNull
    public Optional<U> getLastModifiedBy() {
        return Optional.ofNullable(updatedBy);
    }

    public void setLastModifiedBy(@NonNull U lastModifiedBy) {
        this.updatedBy = lastModifiedBy;
    }

    @NonNull
    public Optional<LocalDateTime> getLastModifiedDate() {
        return Optional.ofNullable(updatedDate);
    }

    public void setLastModifiedDate(@NonNull LocalDateTime lastModifiedDate) {
        this.updatedDate = lastModifiedDate;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    public abstract Long getId();
}
