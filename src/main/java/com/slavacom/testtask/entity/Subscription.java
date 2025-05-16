package com.slavacom.testtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include

    private Long id;

    @NotBlank(message = "Service name is required")
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @ManyToMany(mappedBy = "subscriptions")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

}
