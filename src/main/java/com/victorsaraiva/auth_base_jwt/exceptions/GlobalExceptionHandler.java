package com.victorsaraiva.auth_base_jwt.exceptions;

import com.victorsaraiva.auth_base_jwt.exceptions.user.EmailAlreadyExistsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.InvalidCredentialsException;
import com.victorsaraiva.auth_base_jwt.exceptions.user.UserOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${api.project-name}")
  private String projectName;

  @Value("${api.base-url}")
  private String apiBasePath;

  // ----------------------------------------
  // EXCEÇÕES GENÉRICAS DO SISTEMA
  // ----------------------------------------

  // Handler para erros internos inesperados do sistema que não possuem tratamento específico
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(RuntimeException ex, WebRequest webRequest) {

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ApiErrorType apiErrorType = ApiErrorType.ERRO_DE_SISTEMA;

    ApiError apiError =
        createProblemaBuilder(status, apiErrorType, "Erro no sistema tente novamente").build();

    log.error("Erro genérico inesperado no sistema", ex);
    return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), status, webRequest);
  }

  // ----------------------------------------
  // EXCEÇÕES DE USER
  // ----------------------------------------

  // Handler para erros de email já existente
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Object> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex, WebRequest webRequest) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ApiErrorType apiErrorType = ApiErrorType.CONFLITO_DE_DADOS;

    ApiError apiError = createProblemaBuilder(status, apiErrorType, ex.getMessage()).build();

    log.warn("Email já existe: {}", ex.getMessage());
    return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), status, webRequest);
  }

  // Handler para credenciais inválidas de usuario
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Object> handleInvalidCredentialsException(
      InvalidCredentialsException ex, WebRequest webRequest) {
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    ApiErrorType apiErrorType = ApiErrorType.ERRO_DE_AUTENTICACAO;

    ApiError apiError = createProblemaBuilder(status, apiErrorType, ex.getMessage()).build();

    log.warn("Credenciais inválidas: {}", ex.getMessage());
    return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), status, webRequest);
  }

  // Handler para erros de operação de usuario
  @ExceptionHandler(UserOperationException.class)
  public ResponseEntity<Object> handleUserOperationException(
      UserOperationException ex, WebRequest webRequest) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ApiErrorType apiErrorType = ApiErrorType.OPERACAO_INVALIDA;

    ApiError apiError = createProblemaBuilder(status, apiErrorType, ex.getMessage()).build();

    log.error("Erro de operação de usuario: {}", ex.getMessage());
    return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), status, webRequest);
  }

  // ----------------------------------------
  // EXCEÇÕES DE JWT
  // ----------------------------------------

  // handler para erro de autorização negada
  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<Object> handleAuthorizationDeniedException(
      AuthorizationDeniedException ex, WebRequest webRequest) {
    HttpStatus status = HttpStatus.FORBIDDEN;
    ApiErrorType apiErrorType = ApiErrorType.ACESSO_NEGADO;

    ApiError apiError =
        createProblemaBuilder(
                status, apiErrorType, "Você não tem permissão para acessar este recurso")
            .build();

    log.warn("Acesso negado: {}", ex.getMessage());
    return this.handleExceptionInternal(ex, apiError, new HttpHeaders(), status, webRequest);
  }

  // ----------------------------------------
  // EXCEÇÕES DE VALIDAÇÃO DE OVERRIDES
  // ----------------------------------------

  // Handler para erros do metodo de validação de argumentos do controller
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    ApiErrorType apiErrorType = ApiErrorType.DADOS_INVALIDOS;
    StringBuilder detail = new StringBuilder("Um ou mais campos estão inválidos:\n");
    // Extrai todos os erros de campo e adiciona ao detail
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError ->
                detail
                    .append("- Campo '")
                    .append(fieldError.getField())
                    .append("': ")
                    .append(fieldError.getDefaultMessage())
                    .append("\n"));
    ApiError apiError = createProblemaBuilder(status, apiErrorType, detail.toString()).build();

    log.warn("Validação falhou: {}", detail.toString().trim());
    return handleExceptionInternal(ex, apiError, headers, status, request);
  }

  // Handler para erros de argumento do metodo typeMismatch
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      @NonNull TypeMismatchException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    ApiErrorType apiErrorType = ApiErrorType.PARAMETRO_INVALIDO;
    String detail =
        String.format(
            "O parâmetro de URL '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e tente novamente.",
            ((MethodArgumentTypeMismatchException) ex).getName(), ex.getValue());
    ApiError apiError = createProblemaBuilder(status, apiErrorType, detail).build();

    log.warn("Parâmetro inválido: {} = {}", ex, ex.getValue());
    return handleExceptionInternal(ex, apiError, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      @NonNull NoHandlerFoundException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    ApiErrorType apiErrorType = ApiErrorType.URI_INVALIDA;
    String detail =
        String.format(
            "A URI informada '%s' não existe! Por favor corrija e tente novamente",
            ex.getRequestURL());

    ApiError apiError = createProblemaBuilder(status, apiErrorType, detail).build();

    return this.handleExceptionInternal(ex, apiError, headers, status, request);
  }

  // ----------------------------------------
  // MÉTODOS DE SUPORTE
  // ----------------------------------------

  // Metodo para personalizar o corpo da resposta padrão
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      @NonNull Exception ex,
      @Nullable Object body,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    if (body == null) {
      body =
          ApiError.builder().mensagem(HttpStatus.valueOf(status.value()).getReasonPhrase()).build();
    } else if (body instanceof String string) {
      body = ApiError.builder().mensagem(string).build();
    }

    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  // Metodo auxiliar para construir uma resposta de erro
  private ApiError.ProblemaBuilder createProblemaBuilder(
      HttpStatusCode status, ApiErrorType apiErrorType, String detail) {
    return ApiError.builder()
        .status(status.value())
        .type(apiErrorType.getUri(projectName, apiBasePath))
        .title(apiErrorType.getTitle())
        .detail(detail);
  }
}
