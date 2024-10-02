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
@Entity
@Table(name = "RoleScreenPermission")
public class RoleScreenPermission implements Serializable {

    private static final long serialVersionUID = 584861899224854009L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleScreenPermissions_roleScreenPermission_id_seq")
    @SequenceGenerator(
            name = "roleScreenPermissions_roleScreenPermission_id_seq",
            allocationSize = 1,
            sequenceName = "roleScreenPermissions_roleScreenPermission_id_seq")
    private Long roleScreenPermissionId;

    private Boolean add;

    private Boolean update;

    private Boolean delete;

    private Boolean view;

    private Boolean list;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userRoleId")
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screenId")
    private Screen screen;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleScreenPermission that = (RoleScreenPermission) o;
        return Objects.equals(roleScreenPermissionId, that.roleScreenPermissionId);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
