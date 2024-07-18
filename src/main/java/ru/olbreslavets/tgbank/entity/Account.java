package ru.olbreslavets.tgbank.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.TrueFalseConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "t_accounts")
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private User owner;

    private LocalDate dateBegin;

    private LocalDate dateClose;

    private BigDecimal balance;

    private Boolean isMain;

}
