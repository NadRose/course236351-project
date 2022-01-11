package rest_api

import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import multipaxos.AcceptorService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rpc.TransactionManagerRPCService

@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

	val transactionManagerRPCService = TransactionManagerRPCService()

	val server = ServerBuilder.forPort(9190)
		.apply {
			addService(transactionManagerRPCService)
		}
		.build()

	withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
		server.start()
	}

	runApplication<SpringBootBoilerplateApplication>(*args)
}
