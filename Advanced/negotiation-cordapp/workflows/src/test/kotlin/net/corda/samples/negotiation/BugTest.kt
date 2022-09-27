package net.corda.samples.negotiation

import net.corda.samples.negotiation.flows.BugFlow
import net.corda.samples.negotiation.flows.PropagatingFlow
import net.corda.testing.internal.chooseIdentity
import org.junit.Test

class BugTest : FlowTestsBase() {

    @Test
    fun `does not propagate exception`() {
        val future = a.startFlow(BugFlow(b.info.chooseIdentity()))
        network.runNetwork()
        future.get()
    }

    @Test
    fun `this does propagate exception`() {
        val future = a.startFlow(PropagatingFlow(b.info.chooseIdentity()))
        network.runNetwork()
        future.get()
    }
}