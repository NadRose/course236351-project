package rest_api

import multipaxos.gRPCAndConsensusRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootBoilerplateApplication

//typealias MainFunction =suspend CoroutineScope.(Array<String>, client: ZooKeeperKt) ->Unit
//
//fun mainWith(args:Array<String> = emptyArray(), the_main: MainFunction) = runBlocking {
//	BasicConfigurator.configure()
//
//	val zkSockets = (1..3).map { Pair("127.0.0.1", 2180 + it) }
//	val zkConnectionString = makeConnectionString(zkSockets)
//
//	withZooKeeper(zkConnectionString) {
//		the_main(args, it)
//	}
//}

fun main(args: Array<String>) {
	suspend { gRPCAndConsensusRunner(arrayOf("1")) }
	runApplication<SpringBootBoilerplateApplication>(*args)
}
