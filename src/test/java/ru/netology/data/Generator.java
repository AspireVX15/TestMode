package ru.netology.data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class Generator {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private static final Faker faker = new Faker(new Locale("en"));

    private Generator() {
    }

    private static void sendRequest(String registeredUser) {
        given()
                .spec(requestSpec)
                .body(new Registration.RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    public static String getRandomLogin() {
        String login = faker.name().username();
        return login;
    }

    public static String getRandomPassword() {
        String password = faker.food().fruit();
        return password;
    }

    public static class Registration {
        private Registration() {
        }

        public static RegistrationDto getUser(String status) {
            RegistrationDto user = new RegistrationDto(getRandomLogin(), getRandomPassword(), status);
            return user;
        }

        public static RegistrationDto getRegisteredUser(String status) {
            Gson gson = new Gson();
            RegistrationDto registeredUser = getUser(status);
            given()
                    .spec(requestSpec)
                    .body(gson.toJson(registeredUser))
                    .when()
                    .post("/api/system/users")
                    .then()
                    .statusCode(200);
            return registeredUser;
        }

        @Value
        public static class RegistrationDto {
            String login;
            String password;
            String status;
        }
    }
}
