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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ASUS
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="profile")
public class Profile implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    
    private String firstname;

   
    private String lastname;

    
    private String dateofbirth;

    
    private String imgurl;

   
    private String gender;

  
    private String address;

   @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    
    
    
    
}
