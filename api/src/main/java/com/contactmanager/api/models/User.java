package com.contactmanager.api.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity(name="User")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    @Column(length = 20,nullable = false)
    private String name;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="department_id")
    private Department department;
    @Column(nullable = false,unique = true)
    private long mobile;
    @Column(length = 50,nullable = false,unique = true)
    private String email;
    @OneToMany(cascade = CascadeType.ALL,fetch =FetchType.EAGER,mappedBy = "user")
    private Set<UserType> usertypes;
    @Column(columnDefinition="boolean default true")
    private boolean status;
    @OneToOne(cascade =CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "image")
    private UserFile image;
}
