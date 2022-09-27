package net.corda.samples.negotiation.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.unwrap

@InitiatingFlow
@StartableByRPC
class PropagatingFlow(private val counterparty: Party) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val receiverSession = initiateFlow(counterparty)
        receiverSession.sendAndReceive<Boolean>(true).unwrap { it }
        throw FlowException("Propagating Flow Exception")
    }
}

@InitiatedBy(PropagatingFlow::class)
class PropagatingFlowResponder(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        otherSideSession.receive<Boolean>().unwrap { it }
        try {
            otherSideSession.send(true)
            val signTxFlow = object : SignTransactionFlow(otherSideSession) {
                @Suspendable
                override fun checkTransaction(stx: SignedTransaction) {
                }
            }
            subFlow(signTxFlow)
        } catch (exception: Exception) {
            logger.info("Caught Exception")
            logger.info("Caught ${exception.message}")
        }
    }
}