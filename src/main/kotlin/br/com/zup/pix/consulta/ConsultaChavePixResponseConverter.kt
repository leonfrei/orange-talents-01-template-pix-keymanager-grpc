package br.com.zup.pix.consulta

import br.com.zup.*
import com.google.protobuf.Timestamp
import java.time.ZoneId

class ConsultaChavePixResponseConverter {

    fun convert(chavePix: ChavePixResponse): ConsultaChavePixResponse {
        val instant = chavePix.registradaEm.atZone(ZoneId.of("UTC")).toInstant()
        return ConsultaChavePixResponse.newBuilder()
            .setChave(chavePix.chave)
            .setClienteId(chavePix.clienteId.toString())
            .setPixId(chavePix.pixId.toString())
            .setTipoDeChave(TipoDeChave.valueOf(chavePix.tipoDeChave.name))
            .setConta(
                Conta.newBuilder()
                    .setTipoDeConta(TipoDeConta.valueOf(chavePix.conta.tipoDeConta.name))
                    .setInstituicao(chavePix.conta.instituicao)
                    .setAgencia(chavePix.conta.agencia)
                    .setNumero(chavePix.conta.numeroDaConta)
                    .setTitular(
                        Titular.newBuilder()
                            .setCpf(chavePix.conta.titular.cpf)
                            .setNome(chavePix.conta.titular.nome)
                            .build()
                    )
                    .build()
            )
            .setRegistradaEm(
                Timestamp.newBuilder()
                    .setNanos(instant.nano)
                    .setSeconds(instant.epochSecond)
                    .build()
            ).build()
    }
}
