package com.appsdeveloperblog.tutorials.junit.com.appsdeveloperblog.tutorials.junit.ui;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int localServerPort;

    //Http client to sent request
    @Autowired
    private TestRestTemplate testRestTemplate;

    private  String authorizationToken;

    @Test
    @DisplayName("/post")
    @Order(1)
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws JSONException {
        //Arrange
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Momom");
        userDetailsRequestJson.put("lastName", "Kagroo");
        userDetailsRequestJson.put("email", "toto@gmail.com");
        userDetailsRequestJson.put("password", "123456789");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        //Act
        ResponseEntity<UserRest> createdUserDetailsEntity = testRestTemplate.postForEntity("/users",
                request,
                UserRest.class);
        // get UserRest Object from  ResponseEntity<UserRest> createdUserDetailsEntity object
        UserRest createUserDetails = createdUserDetailsEntity.getBody();

        //Assert
        Assertions.assertEquals(HttpStatus.OK, createdUserDetailsEntity.getStatusCode());
        Assertions.assertEquals(userDetailsRequestJson.getString("firstName"), createUserDetails.getFirstName(),
                "Returned userFirstName semms to be incorrect.");

    }

    @Test
    @DisplayName("get /users failed")
    @Order(2)
    void testGetUser_whenMissingJWT_returns403() {
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity<>( null, headers);

        //Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {
                });
        //Assert

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),"HTTP status code 403 should have been returned");
    }

    @Test
    @DisplayName("/login works")
    @Order(3)
    void testUserLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader() throws JSONException {
        //User login request
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "toto@gmail.com");
        loginCredentials.put("password", "123456789");


        HttpEntity<String> requestEntity = new HttpEntity<>( loginCredentials.toString());

        //Act
        ResponseEntity response = testRestTemplate.postForEntity("/users/login",
                requestEntity,
                null);
        authorizationToken = response.getHeaders()
                .getValuesAsList(SecurityConstants.HEADER_STRING).get(0);

        //Assert

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),"HTTP status code should be 200");
        //if HTTP contains value of JWT
        Assertions.assertNotNull(authorizationToken,
                "Response should contain Authorization header with JWT");
        //if HTTP contains userID
        Assertions.assertNotNull(response.getHeaders()
                        .getValuesAsList("UserID").get(0),
                "Response should contain userID ");

    }

    @Test
    @Order(4)
    @DisplayName("GET /users works")
    void testGetUsers_whenValidJWTProvided_returnsUsers() {
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);
        HttpEntity<String> requestEntity = new HttpEntity<>( headers);


        //Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {
                });

        //As
        Assertions.assertEquals(HttpStatus.OK,
                response.getStatusCode(),
                "HTTP status code should be 200");
        //check if response get exactly 1 user
        Assertions.assertTrue(response.getBody().size() == 1,
                "There should be exacly 1 user in the response ");
    }
}
