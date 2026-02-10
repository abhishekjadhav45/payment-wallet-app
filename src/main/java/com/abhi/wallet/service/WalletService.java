package com.abhi.wallet.service;

import com.abhi.wallet.dto.TransactionResponse;
import com.abhi.wallet.entity.User;
import com.abhi.wallet.entity.Wallet;
import com.abhi.wallet.entity.Transaction;
import com.abhi.wallet.repository.WalletRepository;
import com.abhi.wallet.repository.TransactionRepository;
import com.abhi.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔥 FIXED: User.Role enum + password
    private Wallet getWalletByUserEmail(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(email.split("@")[0]);
            newUser.setPassword(passwordEncoder.encode("wallet123"));
            newUser.setRole(User.Role.USER); // ✅ ENUM instead of String
            return userRepository.save(newUser);
        });

        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setBalance(BigDecimal.valueOf(1000.00));
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
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setUserId(getUserIdByEmail(email));
        transaction.setAmount(amount);
        transaction.setType("CREDIT");
        transaction.setDescription(description != null ? description : "Top-up");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Money added successfully. New balance: ₹" + newBalance;
    }

    public String spendMoney(String email, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be positive";
        }

        Wallet wallet = getWalletByUserEmail(email);
        if (wallet.getBalance().compareTo(amount) < 0) {
            return "Insufficient balance";
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setUserId(getUserIdByEmail(email));
        transaction.setAmount(amount);
        transaction.setType("DEBIT");
        transaction.setDescription(description != null ? description : "Purchase");
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Money spent successfully. New balance: ₹" + newBalance;
    }

    private Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(email.split("@")[0]);
                    newUser.setPassword(passwordEncoder.encode("wallet123"));
                    newUser.setRole(User.Role.USER); // ✅ ENUM
                    return userRepository.save(newUser);
                });
        return user.getId();
    }

    public List<TransactionResponse> getTransactionHistory(String email) {
        Long userId = getUserIdByEmail(email);
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream()
                .limit(10)
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getDescription(),
                        tx.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
