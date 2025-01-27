package pl.projekt.store.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import jakarta.persistence.EntityNotFoundException;
import pl.projekt.store.model.Orders;
import pl.projekt.store.model.Products;
import pl.projekt.store.model.Users;
import pl.projekt.store.service.OrdersService;
import pl.projekt.store.service.ProductsService;
import pl.projekt.store.service.UsersService;

@RestController
@RequestMapping("/api/private/admin")
public class PrivateAdminController {

    @Autowired
    private final UsersService usersService;

    @Autowired
    private final ProductsService productsService;

    @Autowired
    private final OrdersService ordersService;

    public PrivateAdminController(UsersService usersService, ProductsService productsService, OrdersService ordersService) {
        this.usersService = usersService;
        this.productsService = productsService;
        this.ordersService = ordersService;
    }

    /* 
                Produkty
     */


    @Tag(name = "Zastrzeżone metody - produkty", description = "(wymagające logowania)")
    @Operation(
            summary = "Utwórz nowy produkt",
            description = "Dodaje nowy produkt do sklepu. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Produkt został dodany pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(
                                    schema = @Schema(implementation = Products.class)
                                ),
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź sukcesu",
                                        value = "{ \"status\": \"success\", \"message\": \"Dodano poprawnie nowy produkt!\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Błąd przetwarzania danych wejściowych.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @PostMapping("/product")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String image,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock) {
        Map<String, Object> response = new HashMap<>();
        try {
            productsService.createProduct(name, description, image, price, stock);
            response.put("status", "success");
            response.put("message", "Dodano poprawnie nowy produkt!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

@Tag(name = "Zastrzeżone metody - produkty", description = "(wymagające logowania)")
    @Operation(
            summary = "Zaktualizuj istniejący produkt lub dodaj nowy",
            description = "Aktualizuje szczegóły istniejącego produktu w sklepie. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Produkt został zaktualizowany pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"success\", \"message\": \"Zaktualizowano poprawnie produkt o ID: 1\" }"
                                )
                        )
                ),
                @ApiResponse(
                    responseCode = "201",
                    description = "Produkt został stworzony pomyślnie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"status\": \"success\", \"message\": \"Utworzono nowy produkt o ID: 1\" }"
                            )
                    )
            ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Nie znaleziono produktu o podanym ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Produkt o podanym ID nie istnieje.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @PutMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> updateOrCreateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String image,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = productsService.existsById(id);
            if (exists) {
                productsService.updateProduct(id, name, description, image, price, stock);
                response.put("status", "success");
                response.put("message", "Zaktualizowano poprawnie produkt o ID: " + id);
                return ResponseEntity.ok(response);
            } else {
                productsService.createProduct(name, description, image, price, stock);
                response.put("status", "success");
                response.put("message", "Utworzono nowy produkt o ID: " + id);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

@Tag(name = "Zastrzeżone metody - produkty", description = "(wymagające logowania)")
    @Operation(
            summary = "Usuń istniejący produkt",
            description = "Usuwa produkt o podanym ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Produkt został usunięty pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"success\", \"message\": \"Usunięto poprawnie produkt o ID: 1\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Nie znaleziono produktu o podanym ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Produkt o podanym ID nie istnieje.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productsService.deleteProduct(id);
            response.put("status", "success");
            response.put("message", "Usunięto poprawnie produkt o ID: " + id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Tag(name = "Zastrzeżone metody - produkty", description = "(wymagające logowania)")
@Operation(
        summary = "Aktualizuj istniejący produkt pod warunkiem że istnieje",
        description = "Pozwala na aktualizację informacji o istniejącym produkcie. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produkt został zaktualizowany pomyślnie",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Przykładowa odpowiedź sukcesu",
                                    value = "{ \"status\": \"success\", \"message\": \"Zaktualizowano produkt poprawnie!\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"status\": \"error\", \"message\": \"Błąd przetwarzania danych wejściowych.\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Błąd serwera podczas przetwarzania żądania",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                            )
                    )
            )
        }
)
@PatchMapping("/product/{id}")
public ResponseEntity<Map<String, Object>> updateIfExistProduct(
        @PathVariable Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String image,
        @RequestParam(required = false) BigDecimal price,
        @RequestParam(required = false) Integer stock) {
    Map<String, Object> response = new HashMap<>();
    try {
        productsService.updateProduct(id, name, description, image, price, stock);
        response.put("status", "success");
        response.put("message", "Zaktualizowano produkt poprawnie!");
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        response.put("status", "error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
    /* 
                Zamówienia
     */

    @Tag(name = "Zastrzeżone metody - zamówienia", description = "(wymagające logowania)")
    @Operation(
            summary = "Utwórz nowe zamówienie",
            description = "Tworzy nowe zamówienie dla użytkownika na podstawie podanych produktów i statusu. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Zamówienie zostało utworzone pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"success\", \"message\": \"Dodano poprawnie nowe zamówienie!\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam Long userid,
            @RequestParam List<Long> productIds,
            @RequestParam Orders.OrderState status) {
        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal price = ordersService.calculateOrderPrice(productIds);
            ordersService.createOrder(userid, price, status);
            response.put("status", "success");
            response.put("message", "Dodano poprawnie nowe zamówienie!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
@Tag(name = "Zastrzeżone metody - zamówienia", description = "(wymagające logowania)")
@Operation(
    summary = "Usuń istniejące zamówienie",
    description = "Usuwa zamówienie na podstawie podanego ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Zamówienie zostało usunięte pomyślnie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{ \"status\": \"success\", \"message\": \"Zamówienie zostało usunięte pomyślnie\" }"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Błąd walidacji danych lub nieprawidłowe żądanie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Zamówienie o podanym ID nie istnieje",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{ \"status\": \"error\", \"message\": \"Zamówienie nie zostało znalezione.\" }"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Błąd serwera podczas przetwarzania żądania",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                )
            )
        )
    }
)
@DeleteMapping("/order/{orderId}")
public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long orderId) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        ordersService.deleteOrder(orderId);
        response.put("status", "success");
        response.put("message", "Zamówienie zostało usunięte pomyślnie");
        return ResponseEntity.ok(response);
    } 
    catch (EntityNotFoundException e) {
        response.put("status", "error");
        response.put("message", "Zamówienie nie zostało znalezione.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    catch (RuntimeException e) {
        response.put("status", "error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

@Tag(name = "Zastrzeżone metody - zamówienia", description = "(wymagające logowania)")
    @Operation(
            summary = "Pobierz wszystkie zamówienia",
            description = "Zwraca listę wszystkich zamówień wraz z ich szczegółami. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista zamówień została zwrócona pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                array = @ArraySchema(
                                        schema = @Schema(implementation = Orders.class)
                                ),
                                examples = @ExampleObject(
                                        name = "Przykładowa odpowiedź sukcesu",
                                        value = "[\n"
                                        + "  {\n"
                                        + "    \"orderId\": 1,\n"
                                        + "    \"userId\": 1,\n"
                                        + "    \"totalPrice\": 1350,\n"
                                        + "    \"status\": \"completed\",\n"
                                        + "    \"createdAt\": \"2025-01-06T16:30:18.78537\"\n"
                                        + "  },\n"
                                        + "  {\n"
                                        + "    \"orderId\": 2,\n"
                                        + "    \"userId\": 2,\n"
                                        + "    \"totalPrice\": 800,\n"
                                        + "    \"status\": \"pending\",\n"
                                        + "    \"createdAt\": \"2025-01-06T16:30:18.78537\"\n"
                                        + "  },\n"
                                        + "  {\n"
                                        + "    \"orderId\": 3,\n"
                                        + "    \"userId\": 3,\n"
                                        + "    \"totalPrice\": 150,\n"
                                        + "    \"status\": \"shipped\",\n"
                                        + "    \"createdAt\": \"2025-01-06T16:30:18.78537\"\n"
                                        + "  }\n"
                                        + "]"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @GetMapping("/orders")
    public ResponseEntity<List<Orders>> getAllOrders() {
        try {
            List<Orders> orders = ordersService.findAllOrders();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Tag(name = "Zastrzeżone metody - zamówienia", description = "(wymagające logowania)")
    @Operation(
            summary = "Pobierz szczegóły zamówienia",
            description = "Zwraca szczegóły zamówienia na podstawie podanego ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Szczegóły zamówienia zostały zwrócone pomyślnie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{\n"
                                        + "  \"orderId\": 3,\n"
                                        + "  \"userId\": 3,\n"
                                        + "  \"totalPrice\": 150,\n"
                                        + "  \"status\": \"shipped\",\n"
                                        + "  \"createdAt\": \"2025-01-06T16:37:28.954511\"\n"
                                        + "}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Nie znaleziono zamówienia o podanym ID",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Zamówienie o podanym ID nie istnieje.\" }"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Błąd serwera podczas przetwarzania żądania",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                                )
                        )
                )
            }
    )
    @GetMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Orders> optionalOrder = ordersService.getOrderById(id);
            if (optionalOrder.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Zamówienie o podanym ID nie istnieje.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Orders order = optionalOrder.get(); // Pobranie obiektu Orders z Optional
            response.put("orderId", order.getOrderId());
            response.put("userId", order.getUserId());
            response.put("totalPrice", order.getTotalPrice());
            response.put("status", order.getStatus());
            response.put("createdAt", order.getCreatedAt());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /* 
                Użytkownicy
    */

    @Operation(
    summary = "Pobierz wszystkich użytkowników",
    description = "Zwraca listę wszystkich użytkowników. Endpoint wymaga logowania i odpowiednich uprawnień.",
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista użytkowników została odnaleziona",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                              [
                                {
                                  "id": 1,
                                  "username": "admin",
                                  "email": "admin@example.com",
                                  "role": "ADMIN"
                                },
                                {
                                  "id": 2,
                                  "username": "user1",
                                  "email": "user1@example.com",
                                  "role": "USER"
                                }
                              ]"""
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Błąd serwera podczas przetwarzania żądania",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                )
            )
        )
    }
)
@Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
@GetMapping("/users")
public ResponseEntity<?> getAllUsers() {
    try {
        List<Users> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    } catch (Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Wystąpił błąd serwera.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


    ////////////////////////////////////////////////////////////////////////////////////////

     @Operation(
        summary = "Pobierz pojedynczego użytkownika",
        description = "Zwraca dane konkretnego użytkownika na podstawie podanego ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Użytkownik został odnaleziony",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = """
                                  {
                                    "id": 1,
                                    "username": "admin",
                                    "email": "admin@example.com",
                                    "role": "ADMIN"
                                  }"""
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Użytkownik o podanym ID nie został odnaleziony",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Użytkownik nie istnieje.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Błąd serwera podczas przetwarzania żądania",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                    )
                )
            )
        }
    )
    @Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            Users user = usersService.getUserById(id);
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Użytkownik nie istnieje.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Wystąpił błąd serwera.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    
    @Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
    @Operation(
        summary = "Stwórz nowego użytkownika",
        description = "Tworzy nowego użytkownika z podanymi danymi. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Użytkownik został poprawnie stworzony",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"success\", \"message\": \"Użytkownik został poprawnie stworzony!\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Błąd serwera podczas przetwarzania żądania",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/user/")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Users.UserRole role) {
        Map<String, Object> response = new HashMap<>();
        try {
            usersService.createUser(username, email, password, role);
            response.put("status", "success");
            response.put("message", "Użytkownik został poprawnie stworzony!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
    @Operation(
        summary = "Zaktualizuj dane użytkownika",
        description = "Aktualizuje dane użytkownika na podstawie podanego ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Dane użytkownika zostały zaktualizowane",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"success\", \"message\": \"Dane użytkownika zostały zaktualizowane!\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Nie znaleziono użytkownika o podanym ID",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nie znaleziono użytkownika o podanym ID.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Błąd serwera podczas przetwarzania żądania",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Wystąpił błąd serwera.\" }"
                    )
                )
            )
        }
    )

    @PutMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
        @PathVariable Long userId,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) Users.UserRole role) {
        Map<String, Object> response = new HashMap<>();
        try {
            Users user = usersService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + userId));
            usersService.updateUser(user, username, email, password, role);
            response.put("status", "success");
            response.put("message", "Użytkownik został poprawnie zaktualizowany!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
    @Operation(
        summary = "Aktualizuj hasło użytkownika",
        description = "Zmienia hasło użytkownika o podanym ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Hasło użytkownika zostało zaktualizowane pomyślnie",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = Users.class)
                    ),
                    examples = @ExampleObject(
                        name = "Przykładowa odpowiedź sukcesu",
                        value = "{ \"status\": \"success\", \"message\": \"Użytkownik został poprawnie zaktualizowany!\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Nie znaleziono użytkownika o podanym ID",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nie znaleziono użytkownika o ID: 1\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                    )
                )
            )
        }
    )
    @PutMapping("/user/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        Map<String, Object> response = new HashMap<>();
        try {
            Users user = usersService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + userId));
            usersService.updatePassword(user, newPassword);
            response.put("status", "success");
            response.put("message", "Użytkownik został poprawnie zaktualizowany!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Tag(name = "Zastrzeżone metody - użytkownicy", description = "(wymagające logowania)")
    @Operation(
        summary = "Usuń użytkownika",
        description = "Usuwa użytkownika o podanym ID. Endpoint wymaga logowania i odpowiednich uprawnień.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Użytkownik został poprawnie usunięty",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"success\", \"message\": \"Użytkownik został poprawnie usunięty!\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Nie znaleziono użytkownika o podanym ID",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nie znaleziono użytkownika o ID: 1\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Błąd walidacji danych lub nieprawidłowe żądanie",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Nieprawidłowe dane wejściowe.\" }"
                    )
                )
            )
        }
    )
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unused")
            Users user = usersService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o ID: " + userId));
            usersService.deleteUserById(userId);
            response.put("status", "success");
            response.put("message", "Użytkownik został poprawnie usunięty!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    

}