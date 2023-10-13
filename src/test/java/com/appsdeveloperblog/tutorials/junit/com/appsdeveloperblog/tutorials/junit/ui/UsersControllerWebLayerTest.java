package com.appsdeveloperblog.tutorials.junit.com.appsdeveloperblog.tutorials.junit.ui;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.controllers.UsersController;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@MockBean({UsersServiceImpl.class, ...})
public class UsersControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    private UserDetailsRequestModel userDetailsRequestModel ;

    @BeforeEach
    void setup(){
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Momonini");
        userDetailsRequestModel.setLastName("Kevin");
        userDetailsRequestModel.setEmail("toto@gmail.com");
        userDetailsRequestModel.setPassword("123456789");
        userDetailsRequestModel.setRepeatPassword("1234567890");
    }

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        //Arrange

//        UserDto userDto = new UserDto();
//        userDto.setFirstName("Momonini");
//        userDto.setLastName("Kevin");
//        userDto.setEmail("test.toto@gmail.com");
//        userDto.setUserId(UUID.randomUUID().toString());

        UserDto userDto= new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String reponseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createUser = new ObjectMapper().readValue(reponseBodyAsString,UserRest.class);

        //Assert
        Assertions.assertEquals(userDetailsRequestModel.getLastName(), createUser.getLastName(), "the returned last name is most likely incorrect");
        Assertions.assertFalse(createUser.getUserId().isEmpty(), "userId should not be empty");

    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsNotProvided_returns400BadRequest() throws Exception {
        //Arrange
        userDetailsRequestModel.setFirstName("");
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        //Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus(),"Incorrect HTTP Status Code.");

    }

    @Test
    @DisplayName("First should not be less than 2 characters")
    void testCreateUser_whenFirstNameIsLessThan2characters_returns400BadRequest() throws Exception {
        //Arrange
        userDetailsRequestModel.setFirstName("M");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        //Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(),mvcResult.getResponse().getStatus(),"HTTP Status Code is not set to 400.");


    }
}
