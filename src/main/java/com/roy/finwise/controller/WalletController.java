package com.roy.finwise.controller;

import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;
import com.roy.finwise.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletRequest request) {
        WalletResponse walletResponse = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable String id) {
        WalletResponse walletResponse = walletService.getWalletById(id);
        return ResponseEntity.ok(walletResponse);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<WalletResponse>> getAllWalletByUser(@PathVariable String userId) {
        List<WalletResponse> wallets = walletService.getAllWallets(userId);
        return ResponseEntity.ok(wallets);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteWalletById(@PathVariable String id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok("Wallet deleted successfully");
    }
}
