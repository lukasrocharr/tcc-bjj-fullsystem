package com.academia.bjj.academia.mapper;

import com.academia.bjj.academia.dto.AlunoRef;
import com.academia.bjj.academia.dto.AlunoResponse;
import com.academia.bjj.academia.dto.ListaEsperaResponse;
import com.academia.bjj.academia.dto.MatriculaResponse;
import com.academia.bjj.academia.dto.ModalidadeRef;
import com.academia.bjj.academia.dto.ModalidadeResponse;
import com.academia.bjj.academia.dto.PlanoRef;
import com.academia.bjj.academia.dto.PlanoResponse;
import com.academia.bjj.academia.dto.ProfessorRef;
import com.academia.bjj.academia.dto.ProfessorResponse;
import com.academia.bjj.academia.dto.TurmaRef;
import com.academia.bjj.academia.dto.TurmaResponse;
import com.academia.bjj.academia.model.Aluno;
import com.academia.bjj.academia.model.ListaEspera;
import com.academia.bjj.academia.model.Matricula;
import com.academia.bjj.academia.model.Modalidade;
import com.academia.bjj.academia.model.Plano;
import com.academia.bjj.academia.model.Professor;
import com.academia.bjj.academia.model.Turma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapeamento Entidade -> DTO do nucleo da academia (diretriz 2, MapStruct).
 */
@Mapper(componentModel = "spring")
public interface AcademiaMapper {

    // ----- Modalidade -----
    ModalidadeResponse toResponse(Modalidade modalidade);

    ModalidadeRef toRef(Modalidade modalidade);

    // ----- Plano -----
    PlanoResponse toResponse(Plano plano);

    PlanoRef toRef(Plano plano);

    // ----- Professor -----
    @Mapping(target = "usuarioId", source = "usuario.id")
    ProfessorResponse toResponse(Professor professor);

    ProfessorRef toRef(Professor professor);

    // ----- Aluno -----
    @Mapping(target = "usuarioId", source = "usuario.id")
    AlunoResponse toResponse(Aluno aluno);

    AlunoRef toRef(Aluno aluno);

    // ----- Turma -----
    TurmaRef toRef(Turma turma);

    TurmaResponse toResponse(Turma turma, long vagasOcupadas);

    // ----- Matricula -----
    MatriculaResponse toResponse(Matricula matricula);

    // ----- Lista de espera -----
    @Mapping(target = "turmaId", source = "turma.id")
    ListaEsperaResponse toResponse(ListaEspera listaEspera);
}
