// PasswordResetService.java
package org.flow.orderflow.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow.orderflow.dto.password.ForgotPasswordDto;
import org.flow.orderflow.dto.password.ResetPasswordDto;
import org.flow.orderflow.dto.password.ValidateCodeDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.model.User;
import org.flow.orderflow.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
  private final UserRepository userRepository;
  private final MailSenderService mailSenderService;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private final ConcurrentHashMap<String, ResetCodeData> resetCodes = new ConcurrentHashMap<>();

  private static final int CODE_LENGTH = 6;
  private static final int CODE_EXPIRATION_MINUTES = 15;

  public void initiatePasswordReset(ForgotPasswordDto request) {
    log.info("Початок процесу скидання паролю для email: {}", request.email());

    User user = userRepository.findByEmail(request.email())
      .orElseThrow(() -> {
        log.error("Користувача з email {} не знайдено", request.email());
        return new NotFound("Користувача з таким email не знайдено: " + request.email());
      });

    String resetCode = generateResetCode();
    LocalDateTime expirationTime = LocalDateTime.now()
      .plusMinutes(CODE_EXPIRATION_MINUTES);

    resetCodes.put(request.email(), new ResetCodeData(resetCode, expirationTime));

    try {
      String emailText = createResetEmail(resetCode);
      mailSenderService.sendMail(request.email(), "Код для скидання паролю", emailText);
      log.info("Код для скидання паролю успішно відправлено на email: {}",
        request.email());
    } catch (Exception e) {
      log.error("Помилка при відправці email для скидання паролю: {}",
        e.getMessage());
      resetCodes.remove(request.email());
      throw new RuntimeException("Помилка при відправці коду скидання паролю", e);
    }
  }

  public boolean validateResetCode(ValidateCodeDto request) {
    log.debug("Перевірка коду скидання для email: {}", request.email());

    ResetCodeData resetData = resetCodes.get(request.email());
    if (resetData == null) {
      log.debug("Код скидання не знайдено для email: {}", request.email());
      return false;
    }

    if (LocalDateTime.now().isAfter(resetData.expirationTime())) {
      log.debug("Код скидання застарів для email: {}", request.email());
      resetCodes.remove(request.email());
      return false;
    }

    boolean isValid = resetData.code().equals(request.code());
    log.debug("Результат перевірки коду для email {}: {}", request.email(), isValid);
    return isValid;
  }

  @Transactional
  public void resetPassword(ResetPasswordDto request) {
    log.info("Спроба скидання паролю для email: {}", request.email());

    try {
      if (!validateResetCode(new ValidateCodeDto(request.email(), request.code()))) {
        log.error("Спроба скидання паролю з невалідним кодом для email: {}",
          request.email());
        throw new IllegalStateException("Невалідний або застарілий код скидання");
      }

      User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> {
          log.error("Користувача не знайдено при скиданні паролю: {}",
            request.email());
          return new NotFound("Користувача не знайдено: " + request.email());
        });

      String encodedPassword = passwordEncoder.encode(request.newPassword());
      user.setPassword(encodedPassword);

      try {
        userRepository.save(user);
        log.info("Пароль успішно оновлено для користувача: {}", request.email());
      } catch (Exception e) {
        log.error("Помилка збереження нового паролю для {}: {}",
          request.email(), e.getMessage());
        throw new RuntimeException("Помилка збереження нового паролю", e);
      }

      resetCodes.remove(request.email());

    } catch (Exception e) {
      log.error("Помилка при скиданні паролю для {}: {}",
        request.email(), e.getMessage());
      throw new RuntimeException("Помилка скидання паролю: " + e.getMessage(), e);
    }
  }

  private String generateResetCode() {
    Random random = new Random();
    return String.format("%0" + CODE_LENGTH + "d",
      random.nextInt((int) Math.pow(10, CODE_LENGTH)));
  }

  private String createResetEmail(String resetCode) {
    return String.format("""
      Ваш код для скидання паролю: %s

      Цей код дійсний протягом %d хвилин.

      Якщо ви не запитували скидання паролю, проігноруйте цей лист.

      З повагою,
      Команда OrderFlow
      """, resetCode, CODE_EXPIRATION_MINUTES);
  }

  record ResetCodeData(String code, LocalDateTime expirationTime) {
  }
}

