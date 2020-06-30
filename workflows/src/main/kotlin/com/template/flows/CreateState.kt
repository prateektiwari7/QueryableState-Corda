package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.NameContract
import com.template.states.NameState

import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.contracts.requireThat
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

// *********
// * Flows *
// *********

@InitiatingFlow
@StartableByRPC
class NameFlow constructor( var name:String, val UniqueID: String,
                            var Balance : String, var party:Party): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call() : SignedTransaction {

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val command = Command(NameContract.Commands.Create(), listOf(ourIdentity).map { it.owningKey } )
        val namestate = NameState(name,UniqueID,Balance,party,UniqueIdentifier())

        val txBuilder = TransactionBuilder(notary)
                .addOutputState(namestate, NameContract.ID)
                .addCommand(command)
        txBuilder.verify(serviceHub)

        val stx = serviceHub.signInitialTransaction(txBuilder)


        var txResult =  subFlow(FinalityFlow(stx))

        return txResult
    }

}

@InitiatedBy(NameFlow::class)
class NameResponder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call() : SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId.id))
    }
}