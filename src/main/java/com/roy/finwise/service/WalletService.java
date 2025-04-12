package com.roy.finwise.service;

import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;

import java.util.List;

public interface WalletService {
    WalletResponse createWallet(WalletRequest walletRequest);
    WalletResponse getWalletById(String walletId);
    List<WalletResponse> getAllWallets(String userId);
    WalletResponse updateWallet(String walletId, WalletRequest walletRequest);
    void deleteWallet(String walletId);
}
