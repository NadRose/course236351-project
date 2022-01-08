package zookeeper.kotlin.examples

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import zookeeper.kotlin.zookeeper.ZooKeeperKt
import zookeeper.kotlin.zookeeper.ZookeeperKtClient
import java.util.concurrent.Executors

val executor = Executors.newSingleThreadExecutor()
val zkAPIContext = executor.asCoroutineDispatcher()
suspend fun <T> ZooKeeper.withContext(
    block: suspend ZooKeeper.() -> T,
): T = withContext(zkAPIContext) {
    val asyncTask = async { this@withContext.block() }
    asyncTask.await()
}

fun makeConnectionString(sockets: List<Pair<String, Int>>) =
    sockets.joinToString(separator = ",") { (hostname, port) ->
        "${hostname}:${port}"
    }

suspend fun withZooKeeper(
    zkConnectionString: String,
    block: suspend (client: ZooKeeperKt) -> Unit,
) {
    println("--- Connecting to ZooKeeper @ $zkConnectionString")
    val chan = Channel<Unit>()
    val zk = ZooKeeper(zkConnectionString, 1000) { event ->
        if (event.state == Watcher.Event.KeeperState.SyncConnected &&
            event.type == Watcher.Event.EventType.None
        ) {
            runBlocking { chan.send(Unit) }
        }
    }
    chan.receive()
    println("--- Connected to ZooKeeper")
    block(ZookeeperKtClient(zk))
    zk.close()
}
