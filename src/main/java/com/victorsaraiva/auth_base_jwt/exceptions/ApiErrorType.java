package com.victorsaraiva.auth_base_jwt.exceptions;

import lombok.Getter;

@Getter
public enum ApiErrorType {
  ERRO_DE_AUTENTICACAO("Erro de autenticação", "/erro-de-autenticacao"),
  ERRO_DE_SISTEMA("Erro de sistema", "/erro-de-sistema"),
  DADOS_INVALIDOS("Dados inválidos", "/dados-invalidos"),
  PARAMETRO_INVALIDO("Parâmetro inválido", "/parametro-invalido"),
  URI_INVALIDA("URI inválida", "/uri-invalida"),
  ACESSO_NEGADO("Acesso negado", "/acesso-negado"),
  RECURSO_NAO_ENCONTRADO("Recurso não encontrado", "/recurso-nao-encontrado"),
  OPERACAO_INVALIDA("Operação inválida", "/operacao-invalida"),
  CONFLITO_DE_DADOS("Conflito de dados", "/conflito-de-dados");

  private final String title;
  private final String path;

  ApiErrorType(String title, String path) {
    this.title = title;
    this.path = path;
  }

  public String getUri(String projectName, String apiBasePath) {
    return projectName + apiBasePath + this.path;
  }
}
