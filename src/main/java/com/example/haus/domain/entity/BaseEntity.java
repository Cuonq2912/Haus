package com.example.haus.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDate createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    LocalDate updatedAt;

    @Column()
    LocalDate deletedAt;

}
