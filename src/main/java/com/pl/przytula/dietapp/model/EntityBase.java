package com.pl.przytula.dietapp.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "active")
    private boolean active;

    @PrePersist
    private void prePersist(){
        createdOn = LocalDateTime.now();
        updatedOn = null;
        active =true;
    }
    @PreUpdate
    private void preUpdate(){
        updatedOn=LocalDateTime.now();
    }
}
