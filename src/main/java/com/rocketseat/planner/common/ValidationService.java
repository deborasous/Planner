package com.rocketseat.planner.common;

import java.util.List;
import java.util.regex.Pattern;

import jakarta.validation.ValidationException;

public class ValidationService {

  private ValidationService() {
    throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instânciada");
  }

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

  public static void emailValidator(String email) throws ValidationException {
    if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException("E-mail do participante deve ser válido: " + email);
    }
  }

  public static void emailValidators(List<String> emails) throws ValidationException {
    for (String email : emails) {
      emailValidator(email);
    }
  }
}
