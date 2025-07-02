package com.roy.finwise.controller;

import com.roy.finwise.dto.ApiResponse;
import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;
import com.roy.finwise.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet API", description = "All endpoint for wallet related operation")
public class WalletController {

    private final WalletService walletService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(@Valid @RequestBody WalletRequest request) {
        WalletResponse walletResponse = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Wallet creation successful", walletResponse));
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletById(@PathVariable String id) {
        WalletResponse walletResponse = walletService.getWalletById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Wallet retrieval successful", walletResponse));
    }

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getAllWalletByUser(@PathVariable String userId) {
        List<WalletResponse> wallets = walletService.getAllWallets(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Wallets retrieval successful", wallets));
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WalletResponse>> updateWalletById(@PathVariable String id,
                                                                        @Valid @RequestBody WalletRequest walletRequest) {
        WalletResponse walletResponse = walletService.updateWallet(id, walletRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Wallet updation successful", walletResponse));
    }

    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> deleteWalletById(@PathVariable String id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Wallet deleted successfully", null));
    }
}
