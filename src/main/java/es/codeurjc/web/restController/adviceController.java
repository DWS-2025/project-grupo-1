package es.codeurjc.web.restController;

import java.util.NoSuchElementException;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class NoSuchElementExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchElementException.class, AccessDeniedException.class, AuthenticationException.class})
    public String handleErrors(Model model, Exception ex) {
        model.addAttribute("message", "Página no encontrada o acceso denegado.");
        return "error"; // tu plantilla HTML común
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericErrors(Model model, Exception ex) {
        model.addAttribute("message", "Ha ocurrido un error inesperado.");
        return "error";
    }
}