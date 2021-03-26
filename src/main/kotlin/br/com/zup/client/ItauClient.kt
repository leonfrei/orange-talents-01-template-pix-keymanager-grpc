package br.com.zup.client

import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeConta
import br.com.zup.pix.Titular
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.util.*

@Client("http://localhost:9091")
interface ItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas")
    fun buscaContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<ContaResponse>
}

data class ContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): Conta {
        val titularConta = Titular(
            nome = titular.nome,
            cpf = titular.cpf
        )
        return Conta(
            instituicao = instituicao.nome,
            agencia = agencia,
            numeroDaConta = numero,
            titular = titularConta,
            tipoDeConta = TipoDeConta.valueOf(tipo)
        )
    }
}

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)