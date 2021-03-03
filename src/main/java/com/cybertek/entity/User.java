package com.cybertek.entity;

import com.cybertek.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@Where(clause = "is_deleted=false")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"},ignoreUnknown = true)
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passWord;
    private Boolean enabled;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}