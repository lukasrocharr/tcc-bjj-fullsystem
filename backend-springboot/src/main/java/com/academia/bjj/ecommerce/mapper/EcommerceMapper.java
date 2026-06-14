package com.academia.bjj.ecommerce.mapper;

import com.academia.bjj.ecommerce.dto.CategoriaRef;
import com.academia.bjj.ecommerce.dto.CategoriaResponse;
import com.academia.bjj.ecommerce.dto.ItemPedidoResponse;
import com.academia.bjj.ecommerce.dto.PedidoResponse;
import com.academia.bjj.ecommerce.dto.ProdutoResponse;
import com.academia.bjj.ecommerce.dto.VariacaoResponse;
import com.academia.bjj.ecommerce.model.Categoria;
import com.academia.bjj.ecommerce.model.EnderecoEntrega;
import com.academia.bjj.ecommerce.model.ItemPedido;
import com.academia.bjj.ecommerce.model.Pedido;
import com.academia.bjj.ecommerce.model.Produto;
import com.academia.bjj.ecommerce.model.VariacaoProduto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EcommerceMapper {

    CategoriaResponse toResponse(Categoria categoria);

    CategoriaRef toRef(Categoria categoria);

    @Mapping(target = "precoEfetivo", expression = "java(variacao.getPrecoEfetivo())")
    VariacaoResponse toResponse(VariacaoProduto variacao);

    @Mapping(target = "categoria", source = "produto.categoria")
    @Mapping(target = "variacoes", source = "variacoes")
    ProdutoResponse toResponse(Produto produto, List<VariacaoResponse> variacoes);

    ItemPedidoResponse toResponse(ItemPedido item);

    PedidoResponse.EnderecoResponse toEnderecoResponse(EnderecoEntrega endereco);

    @Mapping(target = "criadoEm", source = "createdAt")
    PedidoResponse toResponse(Pedido pedido);
}
