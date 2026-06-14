package com.academia.bjj.auth.model;

/**
 * Papeis (roles) do sistema, conforme RBAC do SRS (RF-098).
 * A authority concedida ao Spring Security e "ROLE_" + name().
 */
public enum PapelNome {
    ALUNO,
    PROFESSOR,
    ADMIN,
    SUPER_ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
