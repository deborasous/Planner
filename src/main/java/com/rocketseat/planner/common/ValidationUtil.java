package com.rocketseat.planner.common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationUtil {

  private ValidationUtil(){
    throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
  }

  public static ApiResponse getErrorResponse(BindingResult result) {
    StringBuilder errorMessage = new StringBuilder("Validação de erros: ");
    for (FieldError error : result.getFieldErrors()) {
      errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
    }
    return new ApiResponse(errorMessage.toString());
  }
}
