package br.com.zup.pix.remove

import br.com.zup.*
import br.com.zup.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChavePixEndpoint(@Inject private val service: RemoveChavePixService) :
    KeymanagerRemoveChavePixGrpcServiceGrpc.KeymanagerRemoveChavePixGrpcServiceImplBase() {

    override fun remover(request: RemoveChavePixRequest?, responseObserver: StreamObserver<RemoveChavePixResponse>?) {
        service.remove(pixId = request!!.pixId, clienteId = request.clienteId)
        responseObserver!!.onNext(
            RemoveChavePixResponse.newBuilder().setPixId(request.pixId).setClienteId(request.clienteId).build()
        )
        responseObserver.onCompleted()
    }

}