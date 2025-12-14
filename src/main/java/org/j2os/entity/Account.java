package org.j2os.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountIdl;

    private Long accountBalance;

    @Size(min = 2, max = 10)
    private String accountOwnerName;

    @Email
    private String accountOwnerMail;


    @Column(nullable = false)
    @NotBlank
    private String accountOwnerAddress;
}