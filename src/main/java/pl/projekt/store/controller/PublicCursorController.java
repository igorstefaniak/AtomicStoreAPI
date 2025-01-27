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
                    value = "[\n"
                            + "    {\n"
                            + "        \"productId\": 1,\n"
                            + "        \"name\": \"Nuka-Cola\",\n"
                            + "        \"description\": \"Klasyczny napój gazowany, który podbił serca mieszkańców przedwojennej Ameryki. Wyjątkowy smak łączy orzeźwiającą słodycz z nutą tajemnicy.\",\n"
                            + "        \"image\": \"https://static.wikia.nocookie.net/fallout/images/1/10/Fallout4_Nuka_Cola.png\",\n"
                            + "        \"price\": 1200,\n"
                            + "        \"stock\": 35,\n"
                            + "        \"createdAt\": \"2025-01-05T22:14:58.673294\"\n"
                            + "    },\n"
                            + "    {\n"
                            + "        \"productId\": 2,\n"
                            + "        \"name\": \"Sunset Sarsaparilla\",\n"
                            + "        \"description\": \"Popularny napój z dzikiego zachodu, ceniony za swój słodki i kremowy smak.\",\n"
                            + "        \"image\": \"https://static.wikia.nocookie.net/fallout/images/d/df/FNV_Sunset_Sarsaparilla.png\",\n"
                            + "        \"price\": 1500,\n"
                            + "        \"stock\": 20,\n"
                            + "        \"createdAt\": \"2025-01-05T22:15:00.673294\"\n"
                            + "    }\n"
                            + "]"
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
                    value = "{\n"
                            + "    \"error\": \"Błąd podczas przetwarzania kursora: szczegóły błędu\"\n"
                            + "}"
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
