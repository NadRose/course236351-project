package rest_api

import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rpc.TransactionManagerRPCService


@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

	// set http server port
	System.setProperty("server.port", args[0])

	val transactionManagerRPCService = TransactionManagerRPCService()

	//set gRPC server port
	val server = ServerBuilder.forPort(args[1].toInt())
		.apply {
			addService(transactionManagerRPCService)
		}
		.build()

	withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
		server.start()
	}

	runApplication<SpringBootBoilerplateApplication>(*args)
}
