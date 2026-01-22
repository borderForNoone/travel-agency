package com.epam.finaltask.dbTest;

import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Slf4j
public class DbRetrieveDataTest {
    @Value("${jwtSecret}")
    String string;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    @Test
    public void testFindUserByName() {
        // The DML script should insert a user with the following id and username:
        String testUserName = "testuser";
        Optional<User> userOptional = userRepository.findUserByUsername(testUserName);

        assertTrue(userOptional.isPresent(), "User should be present in the H2 database");
        User user = userOptional.get();

        // Verify that the user has the expected values
        assertEquals(testUserName, user.getUsername(), "Username should be 'testuser'");
        // You can add more assertions if needed to verify additional fields
    }

    @Test
    public void testFindVoucherById() {
        String testVoucherName = "Amazing Tour";
        UUID userId = UUID.fromString("f3e02ce0-365d-4c03-90a1-98f00cf6d3d1");
        List<Voucher> voucherList = voucherRepository.findAllByUserId(userId);

        Voucher voucher = voucherList.get(0);

        assertEquals(testVoucherName, voucher.getTitle());
    }
}
