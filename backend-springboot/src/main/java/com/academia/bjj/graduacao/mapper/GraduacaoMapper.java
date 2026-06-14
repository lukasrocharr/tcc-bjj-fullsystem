package com.academia.bjj.graduacao.mapper;

import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.graduacao.dto.FaixaRef;
import com.academia.bjj.graduacao.dto.FaixaResponse;
import com.academia.bjj.graduacao.dto.GraduacaoResponse;
import com.academia.bjj.graduacao.model.Faixa;
import com.academia.bjj.graduacao.model.Graduacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AcademiaMapper.class)
public interface GraduacaoMapper {

    FaixaResponse toResponse(Faixa faixa);

    FaixaRef toRef(Faixa faixa);

    GraduacaoResponse toResponse(Graduacao graduacao);
}
