package com.template.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * The family of schemas for IOUState.
 */
object NameStateSchema

/**
 * An IOUState schema.
 */
object NameStateSchemaV1 : MappedSchema(
        schemaFamily = NameStateSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTrans::class.java, NameStatedata::class.java)) {

    @Entity
    @Table(name = "TransactionsTable")
    class PersistentTrans(
            @Id @Column(name = "Id")
            val uuid:UUID,

            @Column(name = "Amount")
            var Amount: String

    ) {
        // Default constructor required by hibernate.
        constructor(): this(UUID.randomUUID(),"")
    }

    @Entity
    @Table(name = "Namedata")
    class NameStatedata(
            @Column(name = "name")
            val name: String,
            @Column(name = "UniqueID")
            val UniqueID: String,
            @Column(name = "Balance")
            val Balance:String,
            @OneToMany(cascade = [CascadeType.PERSIST])
            @JoinColumns(JoinColumn(name = "output_index", referencedColumnName = "output_index"), JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"))
            val claims: List<PersistentTrans>
    ):PersistentState(),Serializable{
        constructor(): this("", "", "", listOf())
    }
}