package com.abhi.wallet.service;

import com.abhi.wallet.entity.User;
import com.abhi.wallet.entity.Wallet;
import com.abhi.wallet.entity.Transaction;
import com.abhi.wallet.repository.WalletRepository;
import com.abhi.wallet.repository.TransactionRepository;
import com.abhi.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor  // ← This ALREADY autowires ALL final fields
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private Wallet getWalletByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    // Create wallet if doesn't exist
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setBalance(BigDecimal.valueOf(1000.00)); // Starting ₹1000
                    newWallet.setCreatedAt(LocalDateTime.now());
                    newWallet.setUpdatedAt(LocalDateTime.now());
                    return walletRepository.save(newWallet);
                });
    }

    public BigDecimal getBalance(String email) {
        Wallet wallet = getWalletByUserEmail(email);
        return wallet.getBalance();
    }

    public String addMoney(String email, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be positive";
        }

        Wallet wallet = getWalletByUserEmail(email);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setUserId(getUserIdByEmail(email));
        transaction.setAmount(amount);
        transaction.setType("CREDIT");
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Money added successfully. New balance: " + wallet.getBalance();
    }

    public String spendMoney(String email, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be positive";
        }

        Wallet wallet = getWalletByUserEmail(email);
        if (wallet.getBalance().compareTo(amount) < 0) {
            return "Insufficient balance";
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setUserId(getUserIdByEmail(email));
        transaction.setAmount(amount);
        transaction.setType("DEBIT");
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Money spent successfully. New balance: " + wallet.getBalance();
    }

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    // Legacy method (kept for backward compatibility)
    public Wallet getOrCreateWalletForCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getWalletByUserEmail(email);
    }

    public BigDecimal getBalance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getBalance(email);
    }
}
