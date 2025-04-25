package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import jakarta.persistence.*;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Article")
public class Article {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    private String title;
    private String url;
    
    private String explanation;
    private String email;
    ZonedDateTime dateAdded;

}
