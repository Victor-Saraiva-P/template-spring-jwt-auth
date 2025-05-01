package com.victorsaraiva.auth_base_jwt.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

  private int status;
  private String type;
  private String title;
  private String detail;

  // Construtor completo
  public ApiError(int status, String type, String title, String detail) {
    this.status = status;
    this.type = type;
    this.title = title;
    this.detail = detail;
  }

  // Implementação manual do Builder
  public static ProblemaBuilder builder() {
    return new ProblemaBuilder();
  }

  public static class ProblemaBuilder {
    private int status;
    private String type;
    private String title;
    private String detail;

    public ProblemaBuilder status(int status) {
      this.status = status;
      return this;
    }

    public ProblemaBuilder type(String type) {
      this.type = type;
      return this;
    }

    public ProblemaBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ProblemaBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public ProblemaBuilder mensagem(String mensagem) {
      this.detail = mensagem;
      return this;
    }

    public ApiError build() {
      return new ApiError(status, type, title, detail);
    }
  }

  // ----- Getters e Setters -----

}
