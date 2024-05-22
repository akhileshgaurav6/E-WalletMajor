package org.WalletService.repository;

import org.WalletService.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findByContact(String contact);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance+:amount where w.contact = :contact")
    void updateWallet(String contact, Double amount);
}
