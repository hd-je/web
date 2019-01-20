package com.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.prefs.Preferences;


@Getter
@NoArgsConstructor
@Entity
@Table
public class User implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private LocalDateTime createData;

    @Column
    private LocalDateTime updateData;

    @Builder
    public User(String name, String password, String email, LocalDateTime createData, LocalDateTime updateData){
        this.name = name;
        this.password = password;
        this.email = email;
        this.createData = createData;
        this.updateData = updateData;
    }
}
