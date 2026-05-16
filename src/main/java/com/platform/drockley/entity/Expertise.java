package com.platform.drockley.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "expertises",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "category"}),
        indexes = @Index(name = "idx_expertise_category", columnList = "category")
)
public class Expertise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @ManyToMany(mappedBy = "expertises", fetch = FetchType.LAZY)
    private Set<ExpertProfile> expertProfiles = new HashSet<>();
}
