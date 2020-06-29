package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.NameContract
import com.template.states.NameState
import com.template.states.TransactionState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
    class DoingPayment(val Tranasactionstateinfo:TransactionStateInfo ,val UniqueID : String) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() :SignedTransaction {
        val NameStateREf = serviceHub.vaultService.queryBy<NameState>().states
        val inputStateAndRef = NameStateREf.filter { it.state.data.UniqueID.equals(UniqueID)}[0]

       val Transactionsdata = TransactionState(Tranasactionstateinfo.Amount,Tranasactionstateinfo.TranscationID)
       // val Transactionsdata = TransactionState("102","x1234567890")

        val input = inputStateAndRef.state.data
        var TransactionList = ArrayList<TransactionState>()
        val command = Command(NameContract.Commands.Create(), listOf(ourIdentity).map { it.owningKey } )

        TransactionList.add(Transactionsdata)

        for (item in input.TransactionState){
            TransactionList.add(item)
        }

        val output = input.copy(TransactionState = TransactionList)

        val txBuilder = TransactionBuilder(inputStateAndRef.state.notary)
                .addInputState(inputStateAndRef)
                .addOutputState(output,NameContract.ID)
                .addCommand(command)

        txBuilder.verify(serviceHub)

        // Sign the transaction
        val stx = serviceHub.signInitialTransaction(txBuilder)


        var txResult =  subFlow(FinalityFlow(stx))

        return txResult

    }
}

@InitiatedBy(DoingPayment::class)
class DoingPayment_responder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call():SignedTransaction {
        subFlow(object : SignTransactionFlow(counterpartySession) {
            @Throws(FlowException::class)
            override fun checkTransaction(stx: SignedTransaction) {
            }
        })
        return subFlow(ReceiveFinalityFlow(counterpartySession))
    }
}


@CordaSerializable
class TransactionStateInfo (val Amount:String, val TranscationID:String)