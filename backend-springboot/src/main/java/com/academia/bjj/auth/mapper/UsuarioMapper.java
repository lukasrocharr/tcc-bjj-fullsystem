package com.academia.bjj.auth.mapper;

import com.academia.bjj.auth.dto.UsuarioResponse;
import com.academia.bjj.auth.model.Papel;
import com.academia.bjj.auth.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapeamento Entidade -> DTO via MapStruct (diretriz 2).
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @org.mapstruct.Mapping(target = "papeis", source = "papeis", qualifiedByName = "papeisToNomes")
    UsuarioResponse toResponse(Usuario usuario);

    @Named("papeisToNomes")
    default List<String> papeisToNomes(Set<Papel> papeis) {
        if (papeis == null) {
            return List.of();
        }
        return papeis.stream()
                .map(p -> p.getNome().name())
                .sorted()
                .collect(Collectors.toList());
    }
}
