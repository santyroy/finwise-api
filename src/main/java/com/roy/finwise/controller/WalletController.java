package com.roy.finwise.controller;

import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;
import com.roy.finwise.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet API", description = "All endpoint for wallet related operation")
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

    @PutMapping("{id}")
    public ResponseEntity<WalletResponse> updateWalletById(@PathVariable String id,
                                                           @Valid @RequestBody WalletRequest walletRequest) {
        WalletResponse walletResponse = walletService.updateWallet(id, walletRequest);
        return ResponseEntity.ok(walletResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteWalletById(@PathVariable String id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok("Wallet deleted successfully");
    }
}
