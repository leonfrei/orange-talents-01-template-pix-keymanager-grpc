package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixResponse
import br.com.zup.KeymanagerConsultaChavePixGrpcServiceGrpc
import br.com.zup.client.BancoCentralClient
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ConsultaChavePixEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: BancoCentralClient,
    @Inject private val validator: Validator
) : KeymanagerConsultaChavePixGrpcServiceGrpc.KeymanagerConsultaChavePixGrpcServiceImplBase() {

    override fun consultar(
        request: ConsultaChavePixRequest,
        responseObserver: StreamObserver<ConsultaChavePixResponse>
    ) {
        val filtro = request.toModel(validator)
        val chavePix = filtro.filtra(repository = repository, bcbClient = bcbClient)
        responseObserver.onNext(ConsultaChavePixResponseConverter().convert(chavePix))
        responseObserver.onCompleted()
    }
}