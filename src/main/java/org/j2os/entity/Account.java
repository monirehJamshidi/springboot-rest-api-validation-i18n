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

    @Size(min = 2, max = 10, message = "{account.owner.name.size}")
    private String accountOwnerName;

    @Email(message = "{account.owner.mail.email}")
    private String accountOwnerMail;


    @Column(nullable = false)
    @NotBlank(message = "{account.owner.address.notblank}")
    private String accountOwnerAddress;
}