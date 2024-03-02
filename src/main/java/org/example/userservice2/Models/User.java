package org.example.userservice2.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity(name="user1")
@Getter
@Setter
public class User extends BaseModel {
    private String email;
    private String password;

    @ManyToMany
    private Set<Role> roles = new HashSet<>();
}
