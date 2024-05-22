package org.WalletService.model;

import jakarta.persistence.*;
import lombok.*;
import org.Utils.UserIdentifier;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;  //by this id we can get what exactly is the amt present insite thhe wallet

    @Column(nullable = false)
    private String contact; //unique identifier,

    @Enumerated(EnumType.STRING)
    private UserIdentifier userIdentifier;

    private String userIdentifierValue; //keep the value of userIentifier as well

    private Double balance;  //to maintain the balance

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}
