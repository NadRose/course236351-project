package rest_api

import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter
import rpc.TransactionManagerRPCService


@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

	System.setProperty("server.port", args[0])

	val transactionManagerRPCService = TransactionManagerRPCService()

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
