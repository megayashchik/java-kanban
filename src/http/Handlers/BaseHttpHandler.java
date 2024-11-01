package http.Handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    private void sendResponse(HttpExchange exchange, String responseText, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, responseText.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseText.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected void sendOkResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    protected void sendCreatedResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 201);
    }

    protected void sendNotFoundResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 404);
    }

    protected void sendHasInteractionsResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 406);
    }

    protected void sendInternalServerErrorResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 500);
    }

    protected void sendNotAllowedResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 405);
    }
}
