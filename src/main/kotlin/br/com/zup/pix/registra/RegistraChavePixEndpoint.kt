package br.com.zup.pix.registra

import br.com.zup.KeymanagerRegistraChavePixGrpcServiceGrpc
import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

//@ErrorHandler
@Singleton
class RegistraChavePixEndpoint(@Inject private val service: RegistraChavePixService) :
    KeymanagerRegistraChavePixGrpcServiceGrpc.KeymanagerRegistraChavePixGrpcServiceImplBase() {

    override fun registrar(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        val novaChave = request!!.toModel()
        val chaveCriada = service.registra(novaChave)

        responseObserver!!.onNext(
            RegistraChavePixResponse.newBuilder().setClienteId(chaveCriada.clienteId.toString())
                .setPixId(chaveCriada.id.toString()).build()
        )
        responseObserver.onCompleted()
    }

}