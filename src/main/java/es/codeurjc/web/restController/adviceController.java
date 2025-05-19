package es.codeurjc.web.restController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * Controller that handles application errors and customizes error messages for the user.
 * <p>
 * Implements {@link ErrorController} to intercept error requests and display user-friendly messages
 * based on the HTTP status code. Uses {@link ErrorAttributes} to extract error details from the request.
 * </p>
 *
 * <ul>
 *   <li>404: "Página no encontrada"</li>
 *   <li>403: "Acceso denegado"</li>
 *   <li>500: "Error interno del servidor"</li>
 *   <li>Other: "Ha ocurrido un error inesperado"</li>
 * </ul>
 *
 * The error message is added to the model and rendered in the "error" view.
 *
 * @author Grupo 1
 */
@Controller
public class adviceController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public adviceController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        String message = (String) errors.get("message");
        Integer status = (Integer) errors.get("status");

        if (message == null || message.isBlank() || "No message available".equals(message)) {
            if (status != null) {
                switch (status) {
                    case 404:
                        message = "Página no encontrada";
                        break;
                    case 403:
                        message = "Acceso denegado";
                        break;
                    case 500:
                        message = "Error interno del servidor";
                        break;
                    default:
                        message = "Ha ocurrido un error inesperado";
                }
            } else {
                message = "Ha ocurrido un error inesperado";
            }
        }

        model.addAttribute("message", message);
        return "error";
    }
}