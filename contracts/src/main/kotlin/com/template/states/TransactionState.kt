package com.template.states

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class TransactionState (val Amount:String, val TranscationID:String)