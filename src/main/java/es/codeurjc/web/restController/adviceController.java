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