package pl.projekt.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import pl.projekt.store.model.Products;
import pl.projekt.store.service.ProductsService;

@RestController
@RequestMapping("/api/private/users")
public class PrivateUserController {

    @Autowired
    private final ProductsService productsService;

    public PrivateUserController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Tag(name = "Zastrzeżone metody - produkty", description = "(wymagające logowania)")
     @Operation(
         summary = "Pobierz wszystkie produkty z sklepu",
         description = "Zwraca listę wszystkich dostępnych produktów w sklepie. Endpoint wymaga logowania i odpowiednich uprawnień.",
         responses = {
             @ApiResponse(
                 responseCode = "200",
                 description = "Lista produktów została zwrócona pomyślnie",
                 content = @Content(
                     mediaType = "application/json",
                     array = @ArraySchema(
                         schema = @Schema(implementation = Products.class)
                     ),
                     examples = @ExampleObject(
                             name = "Przykładowa odpowiedź sukcesu",
                             value = "[\n"
                             + "    {\n"
                             + "    \"productId\": 1,\n"
                             + "    \"name\": \"Nuka-Cola\",\n"
                             + "    \"description\": \"Klasyczny napój gazowany, który podbił serca mieszkańców przedwojennej Ameryki. Wyjątkowy smak łączy orzeźwiającą słodycz z nutą tajemnicy.\",\n"
                             + "    \"image\": \"https://static.wikia.nocookie.net/fallout/images/1/10/Fallout4_Nuka_Cola.png\",\n"
                             + "    \"price\": 1200,\n"
                             + "    \"stock\": 35,\n"
                             + "    \"createdAt\": \"2025-01-05T22:14:58.673294\"\n"
                             + "     }\n" 
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
                         name = "Przykładowa odpowiedź błędu",
                         example = "{ \"error\": \"Internal Server Error\" }"
                     )
                 )
             )
         }
     )
     @GetMapping("/products")
     public List<Products> findAllProducts() {
         return productsService.findAll();
    }
}