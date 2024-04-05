/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aptech.project.coffeeClient.models;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author ASUS
 */
@Data
@Entity
@Table(name = "tbl_uers")
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    private Boolean status;
     private String gender;
   private String birthday;
    private String image;
   
    
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Role.class)
    private Role role_id;
    
     @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;
    
     
}
