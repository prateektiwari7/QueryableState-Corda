package com.template.states

import com.template.contracts.NameContract
import com.template.schema.NameStateSchemaV1
//import com.template.schema.NameSchema1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

//@BelongsToContract(NameContract::class)
//data class NameState(val name: String,
//                     val UniqueID: String,
//                     val Balance : String,
//                     val TransactionState:List<TransactionState> = listOf()) :  QueryableState {
//
//    override val participants: List<AbstractParty> = listOf();
//    //To change initializer of created properties use File | Settings | File Templates.
//
//    override fun generateMappedObject(schema: MappedSchema): PersistentState {
//
//        if (schema is NameStateSchemaV1){
//            var persistentClaims = ArrayList<NameStateSchemaV1.PersistentTrans>()
//            if(TransactionState.isNotEmpty()) {
//                for (item in TransactionState){
//                    persistentClaims.add(NameStateSchemaV1.PersistentTrans(
//                            UUID.randomUUID(),
//                            item.Amount
//                            ))
//                }
//            }
//
//            return NameStateSchemaV1.NameStatedata(
//                    this.name,
//                    this.UniqueID,
//                    this.Balance,
//                    persistentClaims
//            )
//        }else
//            throw IllegalArgumentException("Unsupported Schema")
//
//
//
//
//    }
//    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(NameStateSchemaV1)
//
//}

@BelongsToContract(NameContract::class)
data class NameState(val name: String,
                     val UniqueID: String,
                     val Balance : String,
                     val party: Party,
                     val linearId: UniqueIdentifier,
                     val TransactionState:List<TransactionState> = listOf()) : ContractState, QueryableState {

    override val participants: List<AbstractParty> = listOf(party);
    //To change initializer of created properties use File | Settings | File Templates.

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        if (schema is NameStateSchemaV1) {
            var persistentClaims = ArrayList<NameStateSchemaV1.PersistentTrans>()
            if (TransactionState.isNotEmpty()) {
                for (item in TransactionState) {
                    persistentClaims.add(NameStateSchemaV1.PersistentTrans(
                            UUID.randomUUID(),
                            item.Amount
                    ))
                }


            }



            return NameStateSchemaV1.NameStatedata(
                    this.name,
                    this.UniqueID,
                    this.Balance,
                    persistentClaims
            )

        } else
            throw IllegalArgumentException("Unsupported Schema")


}

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(NameStateSchemaV1)
}



