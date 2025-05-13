package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
//очень много ломбоковских аннотаций, часть из них можно заменить одной @Data
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authors")
public class Author {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;
}
