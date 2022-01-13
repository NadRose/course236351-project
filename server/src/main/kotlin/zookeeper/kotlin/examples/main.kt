package zookeeper.kotlin.examples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import zookeeper.kotlin.zookeeper.ZooKeeperKt

typealias MainFunction =suspend CoroutineScope.(Array<String>, client: ZooKeeperKt) ->Unit

fun mainWith(args:Array<String> = emptyArray(), the_main: MainFunction) = runBlocking {
    BasicConfigurator.configure()

    val zkSockets = (1..3).map { Pair("127.0.0.1", 2180 + it) }
    val zkConnectionString = makeConnectionString(zkSockets)

    withZooKeeper(zkConnectionString) {
        the_main(args, it)
    }
}
