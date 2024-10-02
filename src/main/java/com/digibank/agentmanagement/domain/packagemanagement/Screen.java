package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "screen")
public class Screen implements Serializable {

    private static final long serialVersionUID = 584861899224854010L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screens_screen_id_seq")
    @SequenceGenerator(
            name = "screens_screen_id_seq",
            allocationSize = 1,
            sequenceName = "screens_screen_id_seq")
    private Long screenId;

    @Column(unique=true)
    private String name;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Screen screen = (Screen) o;
        return Objects.equals(screenId, screen.screenId);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
