package com.example.onlineshopsystem_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType type;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;


    @OneToMany(mappedBy = "user")
    private List<Product> products;
}
