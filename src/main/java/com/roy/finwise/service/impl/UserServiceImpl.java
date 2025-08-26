package com.roy.finwise.service.impl;

import com.roy.finwise.dto.*;
import com.roy.finwise.entity.*;
import com.roy.finwise.exceptions.InvalidPeriodException;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.exceptions.UserAlreadyExistException;
import com.roy.finwise.repository.*;
import com.roy.finwise.service.UserService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.getEmail());
        Optional<User> userOpt = userRepository.findByEmail(userRequest.getEmail());
        if (userOpt.isPresent()) {
            log.error("User with email: {} already exist", userRequest.getEmail());
            throw new UserAlreadyExistException("User with email: " + userRequest.getEmail() + " already exists");
        }
        User newUser = MapperUtil.userDtoToEntity(userRequest);
        newUser.setPassword(hashPassword(newUser.getPassword()));
        Role role = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));
        newUser.setRoles(Set.of(role));
        User savedUser = userRepository.save(newUser);
        log.info("User saved with ID: {}", savedUser.getId());
        return MapperUtil.userEntityToDto(savedUser);
    }

    @Override
    public UserResponse findUserById(String userId) {
        return MapperUtil.userEntityToDto(findById(userId));
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        return MapperUtil.userEntityToDto(findByEmail(email));
    }

    @Override
    public UserResponse updateUserByEmail(String email, UserRequest userRequest) {
        User existingUser = findByEmail(email);
        if (userRequest.getName() != null) {
            existingUser.setName(userRequest.getName());
        }
        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            existingUser.setPassword(hashPassword(userRequest.getPassword()));
        }
        if (userRequest.getMobileNumber() != null) {
            existingUser.setMobileNumber(userRequest.getMobileNumber());
        }
        User updatedUser = userRepository.save(existingUser);
        return MapperUtil.userEntityToDto(updatedUser);
    }

    @Override
    public void deleteUserByEmail(String email) {
        User user = findByEmail(email);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUser(user);
        refreshToken.ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();
        userRepository.delete(user);
    }

    @Override
    public DashboardResponse getDashboardDetailsByUser(String userId, String period, int pageNo, int pageSize) {
        try {
            YearMonth periodMonth = YearMonth.parse(period);
            LocalDate startDate = periodMonth.atDay(1);
            LocalDate endDate = periodMonth.atEndOfMonth();
            ZoneId zoneId = ZoneOffset.UTC;
            Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
            Instant endInstant = endDate.plusDays(1).atStartOfDay(zoneId).toInstant();
            User user = findById(userId);
            PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
            List<Transaction> transactions = transactionRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startInstant, endInstant);
            List<TransactionResponse> transactionResponses = transactions.stream().map(MapperUtil::transactionEntityToDto).limit(10).toList();
            BigDecimal totalIncome = calculateTotal(transactions, TransactionType.CREDIT);
            BigDecimal totalExpense = calculateTotal(transactions, TransactionType.DEBIT);
            List<Wallet> wallets = walletRepository.findByUser(user);
            List<DashboardWallet> dashboardWallets = wallets.stream().map(wallet -> new DashboardWallet(wallet.getId().toString(), wallet.getName())).toList();
            return new DashboardResponse(totalIncome, totalExpense, dashboardWallets, transactionResponses);
        } catch (DateTimeParseException ex) {
            throw new InvalidPeriodException("Expected format YYYY-MM");
        }
    }

    @Override
    public AnalyticsResponse getAnalyticsDataByUser(String userId, String period) {
        try {
            YearMonth periodMonth = YearMonth.parse(period);
            LocalDate startDate = periodMonth.atDay(1);
            LocalDate endDate = periodMonth.atEndOfMonth();
            ZoneId zoneId = ZoneOffset.UTC;
            Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
            Instant endInstant = endDate.plusDays(1).atStartOfDay(zoneId).toInstant();
            User user = findById(userId);

            List<Transaction> transactions = transactionRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startInstant, endInstant);

            TransactionAnalytics transactionAnalytics = getTransactionAnalytics(transactions);
            CategoryAnalytics categoryAnalytics = getCategoryAnalytics(transactions);

            return new AnalyticsResponse(transactionAnalytics, categoryAnalytics);
        } catch (DateTimeParseException ex) {
            throw new InvalidPeriodException("Expected format YYYY-MM");
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User with email: {} does not exist", email);
            return new NotFoundException("User with email: " + email + " not found");
        });
    }

    protected User findById(String userId) {
        return userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> {
            log.error("User with ID: {} does not exist", userId);
            return new NotFoundException("User with ID: " + userId + " not found");
        });
    }

    private BigDecimal calculateTotal(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(transaction -> transaction.getType().equals(type))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TransactionAnalytics getTransactionAnalytics(List<Transaction> transactions) {
        List<Transaction> incomeTransactions = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.CREDIT)).toList();
        List<TransactionResponse> incomes = incomeTransactions.stream().map(MapperUtil::transactionEntityToDto).toList();
        List<Transaction> expenseTransactions = transactions.stream().filter(transaction -> transaction.getType().equals(TransactionType.DEBIT)).toList();
        List<TransactionResponse> expenses = expenseTransactions.stream().map(MapperUtil::transactionEntityToDto).toList();
        return new TransactionAnalytics(incomes, expenses);
    }

    private CategoryAnalytics getCategoryAnalytics(List<Transaction> transactions) {
        Map<String, CategorySummary> categorySummaryMap = new HashMap<>();

        for (Transaction tx : transactions) {
            String name = tx.getCategory().getName();
            categorySummaryMap.compute(name, (key, summary) -> {
                if (summary == null) {
                    return new CategorySummary(name, tx.getType(), tx.getAmount());
                } else {
                    BigDecimal newTotal = summary.getTotalAmount().add(tx.getAmount());
                    summary.setTotalAmount(newTotal);
                    return summary;
                }
            });
        }
        List<CategorySummary> categorySummaries = categorySummaryMap.values().stream()
                .sorted((o1, o2) -> o2.getTotalAmount().compareTo(o1.getTotalAmount())).toList();
        return new CategoryAnalytics(categorySummaries);
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
