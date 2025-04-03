package com.initiative79.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class User {
    @Id
    private Long id;

    private String nom;

    private String prenom;

    private String email;
}
