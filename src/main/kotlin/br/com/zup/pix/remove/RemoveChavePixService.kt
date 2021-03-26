package br.com.zup.pix.remove

import br.com.zup.client.BancoCentralClient
import br.com.zup.client.DeletePixKeyRequest
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChavePixInexistenteException
import br.com.zup.shared.validation.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository, @Inject val bcbClient: BancoCentralClient) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "pix ID com formato invalido") pixId: String?,
        @NotBlank @ValidUUID(message = "cliente ID com formato invalido") clienteId: String?
    ) {
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)
        val chave = repository.findByIdAndClienteId(uuidPixId, uuidClienteId).orElseThrow {
            ChavePixInexistenteException("Chave Pix não encontrada ou não pertence ao cliente")
        }
        repository.deleteById(uuidPixId)
        val request = DeletePixKeyRequest(chave.chave)
        val bcbResponse = bcbClient.remover(key = chave.chave, request = request)
        if (bcbResponse.status != HttpStatus.OK){
            throw IllegalStateException("Erro ao remover chave Pix no Banco Central do Brasil (BCB)")
        }
    }
}