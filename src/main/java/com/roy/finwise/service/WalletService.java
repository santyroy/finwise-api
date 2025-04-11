package com.roy.finwise.service;

import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;
import com.roy.finwise.entity.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    WalletResponse createWallet(WalletRequest walletRequest);
    Optional<WalletResponse> getWalletById(String walletId);
    List<WalletResponse> getAllWallets();
    WalletResponse updateWallet(String walletId, WalletRequest walletRequest);
    void deleteWallet(String walletId);
}
