package com.roy.finwise.service.impl;

import com.roy.finwise.dto.WalletRequest;
import com.roy.finwise.dto.WalletResponse;
import com.roy.finwise.entity.User;
import com.roy.finwise.entity.Wallet;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.WalletRepository;
import com.roy.finwise.service.WalletService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserServiceImpl userService;

    @Override
    public WalletResponse createWallet(WalletRequest walletRequest) {
        User user = userService.findById(walletRequest.userId());
        LocalDateTime now = LocalDateTime.now();
        Wallet wallet = Wallet.builder()
                .name(walletRequest.name())
                .spendingLimits(walletRequest.spendingLimits())
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .build();
        Wallet savedWallet = walletRepository.save(wallet);
        return MapperUtil.walletEntityToDto(savedWallet);
    }

    @Override
    public WalletResponse getWalletById(String walletId) {
        Wallet wallet = getWallet(walletId);
        return MapperUtil.walletEntityToDto(wallet);
    }

    @Override
    public List<WalletResponse> getAllWallets(String userId) {
        User user = userService.findById(userId);
        List<Wallet> wallets = walletRepository.findByUser(user);
        return wallets.stream().map(MapperUtil::walletEntityToDto).toList();
    }

    @Override
    public WalletResponse updateWallet(String walletId, WalletRequest walletRequest) {
        return null;
    }

    @Override
    public void deleteWallet(String walletId) {
        Wallet wallet = getWallet(walletId);
        walletRepository.delete(wallet);
    }

    private Wallet getWallet(String walletId) {
        return walletRepository.findById(UUID.fromString(walletId))
                .orElseThrow(() -> new NotFoundException("Wallet with ID: " + walletId + " not found"));
    }
}
