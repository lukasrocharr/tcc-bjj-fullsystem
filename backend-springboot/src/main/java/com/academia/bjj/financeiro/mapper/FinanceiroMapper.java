package com.academia.bjj.financeiro.mapper;

import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.financeiro.dto.MensalidadeResponse;
import com.academia.bjj.financeiro.dto.PagamentoResponse;
import com.academia.bjj.financeiro.model.Mensalidade;
import com.academia.bjj.financeiro.model.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AcademiaMapper.class)
public interface FinanceiroMapper {

    @Mapping(target = "aluno", source = "matricula.aluno")
    @Mapping(target = "plano", source = "matricula.plano.nome")
    @Mapping(target = "valorTotal", expression = "java(mensalidade.getValorTotal())")
    MensalidadeResponse toResponse(Mensalidade mensalidade);

    @Mapping(target = "mensalidadeId", source = "mensalidade.id")
    PagamentoResponse toResponse(Pagamento pagamento);
}
