package com.marcas.exceptions;

import com.marcas.base.BaseController;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@EqualsAndHashCode
public class    ApiExceptionHandler {

    private final static Logger logger = LogManager.getLogger(BaseController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse modelValidations(HttpServletRequest request,MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        return new ErrorResponse(new Exception(message), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            ValidationException.class
    })
    @ResponseBody
    public ErrorResponse badRequest(HttpServletRequest request, Exception exception){
        return new ErrorResponse(exception, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NotFoundException.class
    })
    @ResponseBody
    public ErrorResponse notFoundRequest(HttpServletRequest request, Exception exception) {
        return new ErrorResponse(exception, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            ForbiddenException.class
    })
    @ResponseBody
    public ErrorResponse forbidden(HttpServletRequest request, Exception exception) {
        return new ErrorResponse(exception, request.getRequestURI());
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            UnauthorizedException.class,
            org.springframework.security.access.AccessDeniedException.class
    })
    public void unauthorized() {
        //empty. Nothing to do
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            Exception.class
    })
    @ResponseBody
    public ErrorResponse fatalErrorUnexpected(HttpServletRequest request, Exception exception){
        logger.error(exception.getMessage(), exception);
        return new ErrorResponse(new Exception("500: Error del servidor"), request.getRequestURI());
    }
}
