package br.com.zup.pix.consulta

import br.com.zup.client.BancoCentralClient
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChavePixInexistenteException
import br.com.zup.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixResponse

    @Introspected
    data class PorPixIdEClienteId(
        @field:NotBlank @field:ValidUUID val pixId: String,
        @field:NotBlank @field:ValidUUID val clienteId: String
    ) : Filtro() {
        private val pixIdAsUuid: UUID = UUID.fromString(pixId)
        private val clienteIdAsUuid: UUID = UUID.fromString(clienteId)

        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixResponse {
            return repository.findByIdAndClienteId(pixIdAsUuid, clienteIdAsUuid)
                .map { ChavePixResponse.from(it) }
                .orElseThrow { ChavePixInexistenteException("Chave Pix não encontrada") }
        }
    }

    @Introspected
    data class PorChave(@field:NotBlank @Size(max = 77) val chave: String) : Filtro() {
        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixResponse {
            return repository.findByChave(chave)
                .map { ChavePixResponse.from(it) }
                .orElseGet {
                    logger.info("Chave $chave não encontrada internamente, iniciando busca no BACEN.")
                    val response = bcbClient.consultar(chave)
                    when (response.status) {
                        HttpStatus.OK -> response.body()!!.toModel()
                        else -> throw ChavePixInexistenteException("Chave Pix não encontrada")
                    }
                }
        }
    }

    @Introspected
    object Invalido : Filtro() {
        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixResponse {
            throw IllegalArgumentException("Chave Pix inválida ou nao informada")
        }
    }
}

