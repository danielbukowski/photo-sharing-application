package com.danielbukowski.photosharing.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String login;

    private String password;
}
