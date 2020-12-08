package de.mybureau.time.api;

import de.mybureau.time.service.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(DomainException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDomainException(DomainException domainException) {
        return ErrorDto.from(domainException.getCode(), domainException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleApiValidationException(MethodArgumentNotValidException validException) {
        return ErrorDto.from(1, validException.getBindingResult().toString());
    }
}

class ErrorDto {
    public long code;
    public String message;

    public static ErrorDto from(long code, String message) {
        final var dto = new ErrorDto();
        dto.code = code;
        dto.message = message;
        return dto;
    }
}
