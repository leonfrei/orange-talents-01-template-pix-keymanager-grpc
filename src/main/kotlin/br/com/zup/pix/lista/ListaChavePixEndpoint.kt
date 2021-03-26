package br.com.zup.pix.lista

import br.com.zup.*
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaChavePixEndpoint(@Inject private val repository: ChavePixRepository) :
    KeymanagerListaChavesPixGrpcServiceGrpc.KeymanagerListaChavesPixGrpcServiceImplBase() {

    override fun listar(request: ListaChavesPixRequest, responseObserver: StreamObserver<ListaChavesPixResponse>) {
        if (request.clienteId.isNullOrBlank())
            throw IllegalArgumentException("Cliente ID não pode ser nulo ou vazio")
        val clienteId = UUID.fromString(request.clienteId)
        val chaves = repository.findByClienteId(clienteId).map {
            ListaChavesPixResponse.ChavePix.newBuilder()
                .setChave(it.chave)
                .setPixId(it.id.toString())
                .setTipoDaChave(TipoDeChave.valueOf(it.tipoDeChave.name))
                .setTipoDaConta(TipoDeConta.valueOf(it.conta.tipoDeConta.name))
                .setRegistradaEm(it.registradaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder().setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
        }
        responseObserver.onNext(ListaChavesPixResponse.newBuilder()
            .setClienteId(clienteId.toString())
            .addAllChaves(chaves)
            .build())
        responseObserver.onCompleted()
    }
}