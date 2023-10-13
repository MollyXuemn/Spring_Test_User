package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
public class UserEntityIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    UserEntity userEntity =new UserEntity();

    @BeforeEach
    void setUp(){
        //Arrange
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("Molly");
        userEntity.setLastName("Unary");
        userEntity.setEmail("test@gmail.com");
        userEntity.setEncryptedPassword("12345678");
    }


    @Test
    void testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserDetails() {
        //Arrange

        //Act
        UserEntity storedUserEntity1 = testEntityManager.persistAndFlush(userEntity);

        //Assert
        Assertions.assertTrue(storedUserEntity1.getId() > 0);
        Assertions.assertEquals(userEntity.getUserId(),storedUserEntity1.getUserId());
        Assertions.assertEquals(userEntity.getFirstName(),storedUserEntity1.getFirstName());
        Assertions.assertEquals(userEntity.getLastName(),storedUserEntity1.getLastName());
        Assertions.assertEquals(userEntity.getEmail(),storedUserEntity1.getEmail());
        Assertions.assertEquals(userEntity.getEncryptedPassword(),storedUserEntity1.getEncryptedPassword());
    }

    @Test
    void testUserEntity_whenFirstNameIsTooLongProvided_shouldThrowException() {
        //Arrange
        userEntity.setFirstName("123456789012345678901234567890123456789012345678901");

        //Act
        //Assert
        assertThrows(PersistenceException.class,()->{testEntityManager.persistAndFlush(userEntity);
        }, "Was expecting a PersistenceException to be thrown");

    }

    @Test
    void testUserEntity_whenUserIdIsNotUnique_shouldThrowException() {
        //Arrange
        //Create and Persist a new User Entity
        UserEntity newEntity = new UserEntity();
        newEntity.setUserId("1");
        newEntity.setFirstName("Mollyta");
        newEntity.setLastName("Binary");
        newEntity.setEmail("test2@gmail.com");
        newEntity.setEncryptedPassword("789079879");
        testEntityManager.persistAndFlush(newEntity);

        //Update existing user entity with the same user id
        userEntity.setUserId("1");

        //Act & Assert
        assertThrows(PersistenceException.class,()-> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Was expecting a PersistenceException to be thrown");

    }


}
