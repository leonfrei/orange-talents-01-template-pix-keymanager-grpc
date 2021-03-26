package br.com.zup.pix.registra


import br.com.zup.client.BancoCentralClient
import br.com.zup.client.CreatePixKeyRequest
import br.com.zup.client.ItauClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChavePixExistenteException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegistraChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ItauClient,
    @Inject val bcbClient: BancoCentralClient
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePixRequest): ChavePix {
        //verifica se a chave já existe
        if (repository.existsByChave(novaChave.chave!!))
            throw ChavePixExistenteException("Chave Pix '${novaChave.chave}' existente")
        //busca dados da conta no ERP do ITAU
        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itau")
        //grava no banco de dados
        val chave = novaChave.toModel(conta)
        repository.save(chave)
        //registrando chave no BCB
        val bcbRequest = CreatePixKeyRequest.of(chave).also {
            LOGGER.info("Registrando chave Pix no Banco Central do Brasil (BCB): $it")
        }
        val bcbResponse = bcbClient.registrar(bcbRequest)
        if (bcbResponse.status != HttpStatus.CREATED)
            throw java.lang.IllegalStateException("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)")
        chave.atualizar(bcbResponse.body()!!.key)
        return chave
    }

}
