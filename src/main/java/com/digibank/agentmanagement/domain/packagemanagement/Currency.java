package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "Currency")
public class Currency implements Serializable {

    private static final long serialVersionUID = 584861899224854003L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currencies_currency_id_seq")
    @SequenceGenerator(name = "currencies_currency_id_seq", allocationSize = 1, sequenceName = "currencies_currency_id_seq")
    private Long currencyId;

    @Column(unique=true)
    private String name;

    private String code;

    private String symbol;

    private String rate;

    private Instant createDate;
}
