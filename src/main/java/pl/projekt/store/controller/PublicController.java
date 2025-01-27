package pl.projekt.store.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import pl.projekt.store.model.Users;
import pl.projekt.store.service.ProductsService;
import pl.projekt.store.service.UsersService;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private final UsersService usersService;

    public PublicController(ProductsService productsService, UsersService usersService) {
        this.usersService = usersService;
    }

    
    @Tag(name = "Publiczne metody - użytkownicy", description = "(niewymagające logowania)")
    @Operation(
            summary = "Logowanie użytkownika",
            description = "Pozwala użytkownikowi zalogować się przy użyciu nazwy użytkownika i hasła.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Zalogowano poprawnie",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(
                                    schema = @Schema(implementation = Users.class)
                                ),
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź sukcesu",
                                        value = """
                                                {
                                                  "message": "Zalogowano poprawnie!",
                                                  "user": {
                                                    "userId": 1,
                                                    "username": "admin",
                                                    "email": "admin@example.com",
                                                    "password": "zaszyfrowane_has\u0142o",
                                                    "role": "ADMIN",
                                                    "createdAt": "2025-01-05T22:04:24.707688"
                                                  },
                                                  "status": "success"
                                                }"""
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Błąd uwierzytelniania",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź błędu",
                                        value = """
                                                {
                                                  "status": "error",
                                                  "message": "Nieprawid\u0142owa nazwa u\u017cytkownika lub has\u0142o."
                                                }"""
                                )
                        )
                )
            }
    )
    @GetMapping("/user/login")
    public ResponseEntity<Map<String, Object>> loginUser(
            @RequestParam String username,
            @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            Users user = usersService.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Nieprawidłowa nazwa użytkownika."));

            if (!usersService.verifyPassword(user, password)) {
                throw new RuntimeException("Nieprawidłowe hasło.");
            }

            response.put("status", "success");
            response.put("message", "Zalogowano poprawnie!");
            response.put("user", user);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @Data
    public class ResponseSuccess {

        private String status;
        private String message;
        private Object user;
    }

    @Data
    public class ResponseError {

        private String status;
        private String message;
    }

}
