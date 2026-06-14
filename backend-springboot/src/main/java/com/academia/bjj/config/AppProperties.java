package com.academia.bjj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binding tipado das propriedades "app.*" do application.yml.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Security security = new Security();
    private Cors cors = new Cors();
    private Notification notification = new Notification();
    private Frequencia frequencia = new Frequencia();
    private Graduacao graduacao = new Graduacao();
    private Financeiro financeiro = new Financeiro();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Frequencia getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Frequencia frequencia) {
        this.frequencia = frequencia;
    }

    public Graduacao getGraduacao() {
        return graduacao;
    }

    public void setGraduacao(Graduacao graduacao) {
        this.graduacao = graduacao;
    }

    public Financeiro getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(Financeiro financeiro) {
        this.financeiro = financeiro;
    }

    public static class Jwt {
        private String secret;
        private long accessTokenExpirationMinutes = 30;
        private long refreshTokenExpirationDays = 7;
        private String issuer = "bjj-academy";

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessTokenExpirationMinutes() {
            return accessTokenExpirationMinutes;
        }

        public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
            this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        }

        public long getRefreshTokenExpirationDays() {
            return refreshTokenExpirationDays;
        }

        public void setRefreshTokenExpirationDays(long refreshTokenExpirationDays) {
            this.refreshTokenExpirationDays = refreshTokenExpirationDays;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }
    }

    public static class Security {
        private int maxLoginAttempts = 5;
        private int lockoutMinutes = 15;
        private int passwordResetTokenExpirationMinutes = 30;

        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }

        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }

        public int getLockoutMinutes() {
            return lockoutMinutes;
        }

        public void setLockoutMinutes(int lockoutMinutes) {
            this.lockoutMinutes = lockoutMinutes;
        }

        public int getPasswordResetTokenExpirationMinutes() {
            return passwordResetTokenExpirationMinutes;
        }

        public void setPasswordResetTokenExpirationMinutes(int passwordResetTokenExpirationMinutes) {
            this.passwordResetTokenExpirationMinutes = passwordResetTokenExpirationMinutes;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:4200";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Notification {
        private String from = "no-reply@bjj-academy.local";
        private String provider = "console";

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }

    public static class Frequencia {
        /** Minutos antes do inicio da turma em que o check-in ja e permitido. */
        private int janelaAntesMinutos = 30;
        /** Dias sem check-in que disparam alerta de baixa frequencia (RF-061). */
        private int baixaFrequenciaDias = 15;

        public int getJanelaAntesMinutos() {
            return janelaAntesMinutos;
        }

        public void setJanelaAntesMinutos(int janelaAntesMinutos) {
            this.janelaAntesMinutos = janelaAntesMinutos;
        }

        public int getBaixaFrequenciaDias() {
            return baixaFrequenciaDias;
        }

        public void setBaixaFrequenciaDias(int baixaFrequenciaDias) {
            this.baixaFrequenciaDias = baixaFrequenciaDias;
        }
    }

    public static class Graduacao {
        /** Tempo minimo (dias) na faixa atual para elegibilidade (RF-072). */
        private int diasMinimosElegibilidade = 365;

        public int getDiasMinimosElegibilidade() {
            return diasMinimosElegibilidade;
        }

        public void setDiasMinimosElegibilidade(int diasMinimosElegibilidade) {
            this.diasMinimosElegibilidade = diasMinimosElegibilidade;
        }
    }

    public static class Financeiro {
        /** Dia de vencimento padrao das mensalidades. */
        private int diaVencimento = 10;
        /** Multa fixa aplicada ao atrasar (percentual sobre o valor). */
        private double multaPercentual = 2.0;
        /** Juros por dia de atraso (percentual sobre o valor). */
        private double jurosDiaPercentual = 0.033;
        /** Dias de atraso que bloqueiam o acesso do aluno (RF-081). */
        private int bloqueioDias = 15;

        public int getDiaVencimento() {
            return diaVencimento;
        }

        public void setDiaVencimento(int diaVencimento) {
            this.diaVencimento = diaVencimento;
        }

        public double getMultaPercentual() {
            return multaPercentual;
        }

        public void setMultaPercentual(double multaPercentual) {
            this.multaPercentual = multaPercentual;
        }

        public double getJurosDiaPercentual() {
            return jurosDiaPercentual;
        }

        public void setJurosDiaPercentual(double jurosDiaPercentual) {
            this.jurosDiaPercentual = jurosDiaPercentual;
        }

        public int getBloqueioDias() {
            return bloqueioDias;
        }

        public void setBloqueioDias(int bloqueioDias) {
            this.bloqueioDias = bloqueioDias;
        }
    }
}
