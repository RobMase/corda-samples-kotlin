package net.corda.samples.negotiation.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
@StartableByRPC
class BugFlow(private val counterparty: Party) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val receiverSession = initiateFlow(counterparty)
        receiverSession.send(true)
        throw FlowException("Buggy Flow Exception")
    }
}

@InitiatedBy(BugFlow::class)
class BugFlowResponder(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        otherSideSession.receive<Boolean>()
        try {
            val signTxFlow = object : SignTransactionFlow(otherSideSession) {
                @Suspendable
                override fun checkTransaction(stx: SignedTransaction) {
                }
            }
            subFlow(signTxFlow)
        } catch (exception: Exception) {
            // it does not catch the exception
            logger.info("Caught Exception")
            logger.info("Caught ${exception.message}")
        }
    }
}