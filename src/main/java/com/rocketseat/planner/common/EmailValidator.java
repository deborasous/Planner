package com.rocketseat.planner.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ValidationException;

public class EmailValidator {
  private EmailValidator() {
    throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
}

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");

  public static void validateEmails(List<String> emails) {
    for (String email : emails) {
      validateEmail(email);
    }
  }

  public static void validateEmail(String email) {
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    if (!matcher.matches()) {
      throw new ValidationException("E-mail do participante deve ser válido: " + email);
    }
  }
}
