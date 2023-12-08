package net.larboard.lib.springsimplecrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.larboard.lib.springsimplecrud.CrudModel;

@Entity
@Getter
@Setter
@Table(name = "test_entity")
public class MyModel implements CrudModel<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int age;
    private boolean archived = false;
}
