package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.Message;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class ServerHandlerTest {

    @Test
    public void userRegistration() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SQLException, ClassNotFoundException {
        ServerHandler serverHandler = new ServerHandler(new StoreDataBase());
        PasswordService passwordService = new PasswordService();
        User user = new User("testName", "testSurName", "test@gmail.com", "+000 00 000 00 00");
        user.setBirthday(LocalDate.now());
        byte[] salt = passwordService.generateSalt();
        String password = passwordService.generatePassword();
        user.setSalt(Base64.getEncoder().encodeToString(salt));
        user.setPassword(Base64.getEncoder().encodeToString(passwordService.getEncryptedPassword(password, salt)));
        assertEquals(Message.SUCCESSFUL, serverHandler.userRegistration(user));
        serverHandler.sock.close();
    }
}