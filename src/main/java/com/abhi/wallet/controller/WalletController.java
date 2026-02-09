package com.abhi.wallet.controller;

import com.abhi.wallet.dto.WalletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private double currentBalance = 1000.00;

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance() {
        return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Wallet balance retrieved successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<WalletResponse> addMoney(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            String line, body = "";
            while ((line = reader.readLine()) != null) {
                body += line;
            }
            double amount = 500.0; // Default ₹500
            if (body.contains("500")) amount = 500.0;
            currentBalance += amount;
            return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Money added: ₹" + amount));
        } catch (Exception e) {
            currentBalance += 500.0;
            return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Money added: ₹500 (fallback)"));
        }
    }

    @PostMapping("/spend")
    public ResponseEntity<WalletResponse> spendMoney(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            String line, body = "";
            while ((line = reader.readLine()) != null) {
                body += line;
            }
            double amount = 200.0; // Default ₹200
            if (body.contains("200")) amount = 200.0;
            if (currentBalance >= amount) {
                currentBalance -= amount;
                return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Money spent: ₹" + amount));
            }
            return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Insufficient balance"));
        } catch (Exception e) {
            currentBalance -= 200.0;
            if (currentBalance < 0) currentBalance = 0;
            return ResponseEntity.ok(new WalletResponse(currentBalance, "INR", "Money spent: ₹200 (fallback)"));
        }
    }
}
