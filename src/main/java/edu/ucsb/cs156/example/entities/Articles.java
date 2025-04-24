package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Articles")
public class Articles {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    private String url;
    private String explanation;
    private String email;
    LocalDateTime dateAdded;

}
