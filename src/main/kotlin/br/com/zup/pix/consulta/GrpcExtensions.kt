package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.CHAVE
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.PIXIDECLIENTEID
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException

fun ConsultaChavePixRequest.toModel(validator: Validator): Filtro {
    val filtro = when (consultaCase) {
        PIXIDECLIENTEID -> pixIdEClienteId.let { Filtro.PorPixIdEClienteId(pixId = it.pixId, clienteId = it.clienteId)}
        CHAVE -> Filtro.PorChave(chave)
        else -> throw IllegalArgumentException("Chave inválida ou não informada.")
    }

    val validations = validator.validate(filtro)
    if (validations.isNotEmpty())
        throw ConstraintViolationException(validations)

    return filtro
}