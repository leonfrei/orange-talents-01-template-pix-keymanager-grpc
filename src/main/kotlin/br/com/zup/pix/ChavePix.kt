package br.com.zup.pix

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "uk_chave_pix", columnNames = ["chave"])])
class ChavePix(
    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID,

    @field:NotNull
    @Enumerated(STRING)
    @Column(nullable = false)
    val tipoDeChave: TipoDeChave,

    @field:NotBlank
    @field:Size(max = 77)
    @Column(nullable = false, unique = true)
    var chave: String,

    @field:Valid
    @Embedded
    val conta: Conta
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null

    @field:NotNull
    @Column(nullable = false)
    val registradaEm: LocalDateTime = LocalDateTime.now()

    fun atualizar(novaChave: String) {
        if (tipoDeChave == TipoDeChave.CHAVE_ALEATORIA)
            chave = novaChave
    }

}
