package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    FileService fileService = Mockito.mock(FileService.class);
    UserService userService = new UserService(mockUserRepository, fileService);

    @Test
    void testGetUsers() {
        SqlRowSet mockResult = Mockito.mock(SqlRowSet.class);
        List<User> expectedUserList = new ArrayList<>();
        User user1 = new User(1,"TestName1","testName1@example.com" ,"testLastHandShake1", "testAllowedIps1");
        User user2 = new User(2,"TestName2","testName2@example.com" ,"testLastHandShake2", "testAllowedIps2");
        expectedUserList.add(user1);
        expectedUserList.add(user2);

        Mockito.when(mockResult.next()).thenReturn(true, true, false);
        Mockito.when(mockResult.getInt("id")).thenReturn(1, 2);
        Mockito.when(mockResult.getString("username")).thenReturn("TestName1", "TestName2");
        Mockito.when(mockResult.getString("email")).thenReturn("testName1@example.com", "testName2@example.com");
        Mockito.when(mockResult.getString("allowedIPs")).thenReturn("testAllowedIps1", "testAllowedIps2");
        Mockito.when(mockResult.getString("lastHandShake")).thenReturn("testLastHandShake1", "testLastHandShake2");
        Mockito.when(mockUserRepository.getAllUsers()).thenReturn(mockResult);

        List<User> actualUserList = userService.getUsers();
        Assertions.assertEquals(expectedUserList.toString(), actualUserList.toString());
    }

    @Test
    void addUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void downloadFile() {
    }
}


