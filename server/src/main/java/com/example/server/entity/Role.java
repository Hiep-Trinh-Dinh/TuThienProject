package com.example.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Objects;

@Getter
@Setter
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@ToString(exclude = "permissions")
public class Role {
    @Id
    @Column(length = 50)
    String name;
    
    @Column(length = 255)
    String description;

    @ManyToMany
    @Builder.Default
    Set<Permission> permissions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
