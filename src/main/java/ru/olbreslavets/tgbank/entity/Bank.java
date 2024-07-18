package ru.olbreslavets.tgbank.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Table(name = "t_banks")
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String bik;

    private String name;

    private String legalAddress;

    private String corrAcc;

    @OneToOne
    @JoinColumn(name = "contacts")
    private Contact contacts;

}
