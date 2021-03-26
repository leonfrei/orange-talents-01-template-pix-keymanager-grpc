package br.com.zup.pix.registra

import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeConta
import br.com.zup.shared.validation.ValidPixKey
import br.com.zup.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePixRequest(
    @ValidUUID @field:NotBlank val clienteId: String?,
    @field:NotNull val tipoDeChave: TipoDeChave?,
    @field:Size(max = 77) val chave: String?,
    @field:NotNull val tipoDeConta: TipoDeConta?
) {
    fun toModel(conta: Conta): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipoDeChave = TipoDeChave.valueOf(this.tipoDeChave!!.name),
            chave = if (this.tipoDeChave == TipoDeChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else chave!!,
            conta = conta
        )
    }
}
