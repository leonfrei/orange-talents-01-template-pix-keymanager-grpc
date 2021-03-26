package br.com.zup.pix.registra

import br.com.zup.RegistraChavePixRequest
import br.com.zup.TipoDeChave.UNKNOWN_TIPO_DE_CHAVE
import br.com.zup.TipoDeConta.UNKNOWN_TIPO_DE_CONTA
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.TipoDeConta

fun RegistraChavePixRequest.toModel() : NovaChavePixRequest {
    return NovaChavePixRequest(
        clienteId = clienteId,
        tipoDeChave = when (tipoDeChave) {
            UNKNOWN_TIPO_DE_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name)
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            UNKNOWN_TIPO_DE_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}