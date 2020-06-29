package com.template.webserver

import net.corda.core.messaging.startFlow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.template.flows.DoingPayment
import com.template.flows.TransactionStateInfo
import org.springframework.http.MediaType

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    private val me = proxy.nodeInfo().legalIdentities.first().name


    @GetMapping(value = [ "me" ], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun whoami() = mapOf("me" to me.toString())


    @GetMapping(value = ["/templateendpoint"], produces = ["text/plain"])
    private fun templateendpoint(): String {
        return "Define an endpoint here."
    }


    @PostMapping(value = ["/TransactionState/TransactionID/{UniqueID}"])
    fun claim(@RequestBody transactionsstate:TransactionStateInfo, @PathVariable UniqueID:String): ResponseEntity<String> {
        print("---------")
        print(transactionsstate)
        print("---------")
        print(UniqueID)
        print("---------")
        return try {
            val stx = proxy.startFlow(::DoingPayment,transactionsstate,UniqueID)
            ResponseEntity.status(HttpStatus.CREATED).body("Claim filed ${stx.id}")

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }




}