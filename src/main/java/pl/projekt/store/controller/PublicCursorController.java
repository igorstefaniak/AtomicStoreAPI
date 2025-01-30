package pl.projekt.store.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/public")
public class PublicCursorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Tag(name = "Publiczne metody (używające kursora)", description = "(niewymagające logowania)")
    @Operation(
            summary = "Pobierz dane z bazy za pomocą kursora",
            description = "Zwraca listę rekordów z bazy danych przy użyciu kursora.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista produktów",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        type = "array"
                                ),
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź sukcesu",
                                        value = """
                            [
                                {
                                    "productId": 1,
                                    "name": "Nuka-Cola",
                                    "description": "Klasyczny nap\u00f3j gazowany, kt\u00f3ry podbi\u0142 serca mieszka\u0144c\u00f3w przedwojennej Ameryki. Wyj\u0105tkowy smak \u0142\u0105czy orze\u017awiaj\u0105c\u0105 s\u0142odycz z nut\u0105 tajemnicy.",
                                    "image": "https://static.wikia.nocookie.net/fallout/images/1/10/Fallout4_Nuka_Cola.png",
                                    "price": 1200,
                                    "stock": 35,
                                    "createdAt": "2025-01-05T22:14:58.673294"
                                },
                                {
                                    "productId": 2,
                                    "name": "Sunset Sarsaparilla",
                                    "description": "Popularny nap\u00f3j z dzikiego zachodu, ceniony za sw\u00f3j s\u0142odki i kremowy smak.",
                                    "image": "https://static.wikia.nocookie.net/fallout/images/d/df/FNV_Sunset_Sarsaparilla.png",
                                    "price": 1500,
                                    "stock": 20,
                                    "createdAt": "2025-01-05T22:15:00.673294"
                                },
                                {
                                    "productId": 3,
                                    "name": "Quantum Nuka-Cola",
                                    "description": "Limitowana edycja Nuka-Coli z dodatkiem radioaktywnego blasku. Zwiększa energię i podnosi morale!",
                                    "image": "https://static.wikia.nocookie.net/fallout/images/9/9e/Fallout4_Nuka_Cola_Quantum?.png",
                                    "price": 2500,
                                    "stock": 10,
                                    "createdAt": "2025-01-06T10:30:15.123456"
                                }
                            ]"""
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź błędu",
                                        value = """
                            {
                                "error": "B\u0142\u0105d podczas przetwarzania kursora: szczeg\u00f3\u0142y b\u0142\u0119du"
                            }"""
                                )
                        )
                )
            }
    )
    @GetMapping("/cursor/products")
    public List<Map<String, Object>> getDataWithCursor() {
        List<Map<String, Object>> result = new ArrayList<>();

        jdbcTemplate.execute((Connection connection) -> {
            try (Statement statement = connection.createStatement()) {

                ResultSet resultSet = statement.executeQuery("SELECT product_id, name, price FROM Products");

                // iterowanie po wynikach za pomocą kursora
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>(); // zachowuje kolejność dodawania elementów
                    row.put("productId", resultSet.getInt("product_id"));
                    row.put("name", resultSet.getString("name"));
                    row.put("price", resultSet.getBigDecimal("price"));
                    result.add(row);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Błąd podczas przetwarzania kursora: " + e.getMessage());
            }
            return null;
        });

        return result;
    }

}
