package com.academia.bjj.frequencia.mapper;

import com.academia.bjj.academia.mapper.AcademiaMapper;
import com.academia.bjj.frequencia.dto.CheckInResponse;
import com.academia.bjj.frequencia.model.CheckIn;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AcademiaMapper.class)
public interface FrequenciaMapper {

    CheckInResponse toResponse(CheckIn checkIn);
}
