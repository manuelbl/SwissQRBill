//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.web.model.ValidationMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageLocalizer messageLocalizer;

    /**
     * Creates an instance.
     * <p>
     * Single constructor for Spring dependency injection.
     * </p>
     */
    public RestResponseEntityExceptionHandler(MessageLocalizer messageLocalizer) {
        this.messageLocalizer = messageLocalizer;
    }

    @ExceptionHandler(QRBillValidationError.class)
    protected ResponseEntity<Object> handleValidationError(QRBillValidationError ex, WebRequest request) {
        List<ValidationMessage> messages
                = QrBillDTOConverter.toDtoValidationMessageList(ex.getValidationResult().getValidationMessages());
        messageLocalizer.addLocalMessages(messages);
        return handleExceptionInternal(ex, messages, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected ResponseEntity<Object> messageConversionExceptionHandler(HttpMessageConversionException ex, WebRequest request) {
        Throwable cause = ex;
        while (cause.getCause() != null && cause.getCause() != cause)
            cause = cause.getCause();
        return handleExceptionInternal(ex, cause.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
