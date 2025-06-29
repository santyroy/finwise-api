package com.roy.finwise.db;

import com.roy.finwise.entity.Category;
import com.roy.finwise.entity.Role;
import com.roy.finwise.entity.TransactionType;
import com.roy.finwise.repository.CategoryRepository;
import com.roy.finwise.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeCategories();
        initializeRoles();
    }

    private void initializeCategories() {
        List<Category> categories = List.of(
                Category.builder().name("GROCERIES").type(TransactionType.DEBIT).build(),
                Category.builder().name("TRANSPORTATION").type(TransactionType.DEBIT).build(),
                Category.builder().name("UTILITIES").type(TransactionType.DEBIT).build(),
                Category.builder().name("RENT").type(TransactionType.DEBIT).build(),
                Category.builder().name("ENTERTAINMENT").type(TransactionType.DEBIT).build(),
                Category.builder().name("DINING_OUT").type(TransactionType.DEBIT).build(),
                Category.builder().name("HEALTH_MEDICAL").type(TransactionType.DEBIT).build(),
                Category.builder().name("SHOPPING").type(TransactionType.DEBIT).build(),
                Category.builder().name("SUBSCRIPTIONS").type(TransactionType.DEBIT).build(),
                Category.builder().name("INSURANCE").type(TransactionType.DEBIT).build(),
                Category.builder().name("EDUCATION").type(TransactionType.DEBIT).build(),
                Category.builder().name("HOME_MAINTENANCE").type(TransactionType.DEBIT).build(),
                Category.builder().name("BILLS").type(TransactionType.DEBIT).build(),
                Category.builder().name("DONATIONS").type(TransactionType.DEBIT).build(),

                Category.builder().name("SALARY").type(TransactionType.CREDIT).build(),
                Category.builder().name("FREELANCE_INCOME").type(TransactionType.CREDIT).build(),
                Category.builder().name("BUSINESS_INCOME").type(TransactionType.CREDIT).build(),
                Category.builder().name("INVESTMENTS").type(TransactionType.CREDIT).build(),
                Category.builder().name("RENT_INCOME").type(TransactionType.CREDIT).build(),
                Category.builder().name("GIFTS").type(TransactionType.CREDIT).build(),
                Category.builder().name("BONUSES").type(TransactionType.CREDIT).build(),
                Category.builder().name("TAX_REFUNDS").type(TransactionType.CREDIT).build(),
                Category.builder().name("CASHBACK_REWARDS").type(TransactionType.CREDIT).build(),
                Category.builder().name("PENSION_RETIREMENT").type(TransactionType.CREDIT).build(),
                Category.builder().name("ROYALTIES").type(TransactionType.CREDIT).build(),
                Category.builder().name("MISCELLANEOUS_INCOME").type(TransactionType.CREDIT).build()
        );

        // Filter out categories that already exist in the database
        List<Category> newCategories = categories.stream()
                .filter(category -> categoryRepository.findByName(category.getName()).isEmpty()).toList();

        // Save all new categories at once
        if (!newCategories.isEmpty()) {
            log.info("Adding system generated categories to application");
            categoryRepository.saveAll(newCategories);
        }
    }

    private void initializeRoles() {
        List<Role> roles = List.of(
                new Role("USER"),
                new Role("ADMIN")
        );
        List<Role> newRoles = roles.stream().filter(role -> roleRepository.findByName(role.getName()).isEmpty()).toList();
        if (!newRoles.isEmpty()) {
            log.info("Adding system generated roles to application");
            roleRepository.saveAll(newRoles);
        }
    }
}
