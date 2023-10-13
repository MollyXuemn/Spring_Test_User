package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UsersRepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UsersRepository usersRepository;

    UserEntity userEntity =new UserEntity();
    private final String email1 = "test1@test.com";
    private final String email2 = "test2@test.com";


    @BeforeEach
    void setUp(){
        //Arrange
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("Molly");
        userEntity.setLastName("Unary");
        userEntity.setEmail(email1);
        userEntity.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(userEntity);
    }
    @Test
    void testFindByEmail_withEmail_returnUserEntity (){

        //Arrange

        //Act
        UserEntity storedUser = usersRepository.findByEmail(userEntity.getEmail());
        //Assert
        assertEquals(userEntity.getEmail(), storedUser.getEmail(),"returned email address does not match the expected value ");
    }

    @Test
    void testFindByUserId_withUserIdProvided_shouldReturnStoredUserDetails (){
        //Arrange
        //Create and Persist a new User Entity
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUserId("2");
        userEntity2.setFirstName("Mollyta");
        userEntity2.setLastName("Binary");
        userEntity2.setEmail(email2);
        userEntity2.setEncryptedPassword("789079879");
        testEntityManager.persistAndFlush(userEntity2);

        //Act
        UserEntity storedUser = usersRepository.findByUserId(userEntity2.getUserId());
        //Assert
        assertNotNull(storedUser,"UserEntity object should not be null");
            //test only the userEntity2's id if they are same with the stored user id in the db
        assertEquals(userEntity2.getUserId(), storedUser.getUserId(),
                "returned userId does not match the expected value ");
    }
    @Test
    void testFindUsersWithEmailEndingWith_withGivenEmailDomain_shouldReturnUsersWithGivenDomain (){
        //Arrange
        //Create and Persist a new User Entity
        UserEntity userEntity3 = new UserEntity();
        userEntity3.setUserId("3");
        userEntity3.setFirstName("MimiNo");
        userEntity3.setLastName("January");
        userEntity3.setEmail("test3@gmail.com");
        userEntity3.setEncryptedPassword("789079879");
        testEntityManager.persistAndFlush(userEntity3);

        String emailDomainName = "@gmail.com";
        //Act
        List<UserEntity> users = usersRepository.findUsersWithEmailEndingWith(emailDomainName);
        //Assert
        assertEquals(1,users.size(), "there should be only one user in the list");

        assertTrue(users.get(0).getEmail().endsWith(emailDomainName));

    }

}
