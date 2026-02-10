package com.abhi.wallet.controller;

import com.abhi.wallet.dto.TransactionResponse;
import com.abhi.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam String email) {
        BigDecimal balance = walletService.getBalance(email);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMoney(
            @RequestParam String email,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        String result = walletService.addMoney(email, amount, description);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/spend")
    public ResponseEntity<String> spendMoney(
            @RequestParam String email,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        String result = walletService.spendMoney(email, amount, description);
        return ResponseEntity.ok(result);
    }

    // 🔥 DAY 6: Transaction History
    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@RequestParam String email) {
        List<TransactionResponse> history = walletService.getTransactionHistory(email);
        return ResponseEntity.ok(history);
    }
}
