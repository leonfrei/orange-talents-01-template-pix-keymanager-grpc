package br.com.zup.pix.consulta

import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import java.time.LocalDateTime
import java.util.*

data class ChavePixResponse(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoDeChave: TipoDeChave,
    val chave: String,
    val conta: Conta,
    val registradaEm: LocalDateTime
) {

    companion object {
        fun from(chave: ChavePix): ChavePixResponse {
            return ChavePixResponse(
                pixId = chave.id,
                clienteId = chave.clienteId,
                tipoDeChave = chave.tipoDeChave,
                chave = chave.chave,
                conta = chave.conta,
                registradaEm = chave.registradaEm
            )
        }
    }
}