package br.com.zup.client

import br.com.zup.Instituicoes
import br.com.zup.pix.*
import br.com.zup.pix.consulta.ChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime


@Client("http://localhost:8082")
interface BancoCentralClient {
    @Post(
        "/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun registrar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun remover(@PathVariable key: String, @Body request: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun consultar(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.ISPB
)

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)

data class CreatePixKeyRequest(
    val key: String,
    val pixKeyType: PixKeyType,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    companion object {
        fun of(chavePix: ChavePix): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                key = chavePix.chave,
                pixKeyType = PixKeyType.from(chavePix.tipoDeChave),
                bankAccount = BankAccount(
                    participant = Conta.ISPB,
                    branch = chavePix.conta.agencia,
                    accountNumber = chavePix.conta.numeroDaConta,
                    accountType = AccountType.from(chavePix.conta.tipoDeConta)
                ),
                owner = Owner(
                    type = OwnerType.NATURAL_PERSON,
                    name = chavePix.conta.titular.nome,
                    taxIdNumber = chavePix.conta.titular.cpf
                )
            )
        }
    }
}

data class PixKeyDetailsResponse(
    val pixKeyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixResponse {
        return ChavePixResponse(
            tipoDeChave = pixKeyType.tipoDeChave!!,
            chave = key,
            conta = Conta(
                instituicao = Instituicoes.INSTITUICOES[bankAccount.participant]!!,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber,
                titular = Titular(nome = owner.name, cpf = owner.taxIdNumber),
                tipoDeConta = bankAccount.accountType.tipoDeConta
            ),
            registradaEm = createdAt
        )
    }
}

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
)

enum class OwnerType {
    NATURAL_PERSON, LEGAL_PERSON
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)

enum class AccountType(val tipoDeConta: TipoDeConta) {
    CACC(TipoDeConta.CONTA_CORRENTE),
    SVGS(TipoDeConta.CONTA_POUPANCA);

    companion object {
        fun from(tipo: TipoDeConta): AccountType {
            val accountTypeMap = AccountType.values().associateBy(AccountType::tipoDeConta)
            return accountTypeMap[tipo] ?: throw IllegalArgumentException("AccountType inexistente para $tipo")
        }
    }

}

enum class PixKeyType(val tipoDeChave: TipoDeChave?) {
    CPF(TipoDeChave.CPF),
    CNPJ(null),
    PHONE(TipoDeChave.TELEFONE_CELULAR),
    EMAIL(TipoDeChave.EMAIL),
    RANDOM(TipoDeChave.CHAVE_ALEATORIA);

    companion object {
        fun from(tipo: TipoDeChave): PixKeyType {
            val keyTypeMap = PixKeyType.values().associateBy(PixKeyType::tipoDeChave)
            return keyTypeMap[tipo] ?: throw IllegalArgumentException("KeyType inexistente para $tipo")
        }
    }

}

data class CreatePixKeyResponse(
    val pixKeyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)
