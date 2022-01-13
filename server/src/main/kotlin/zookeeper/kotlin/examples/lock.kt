package zookeeper.kotlin.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import zookeeper.kotlin.zookeeper.ZKPaths
import zookeeper.kotlin.zookeeper.ZooKeeperKt
import zookeeper.kotlin.createflags.Ephemeral
import zookeeper.kotlin.createflags.Sequential

fun main(args: Array<String>) = mainWith(args) { _, zk ->
    val zkmutex = ZKMutex.make(zk, "my-lock")
    println("Waiting for the lock....")
    zkmutex.lock()
    println("I Have the lock!!!")
    delay(7_000)
    println("Releasing the lock!!!")
    zkmutex.unlock()
}

class ZKMutex private constructor(private val zk: ZooKeeperKt, val lockname: String) {
    companion object {
        suspend fun make(zk: ZooKeeperKt, lockname: String): ZKMutex {
            val zk = zk
                .usingNamespace("/locks")
                .usingNamespace("/$lockname")
            return ZKMutex(zk, lockname)
        }
    }

    var mySeqNo: String? = null
    suspend fun lock() {
        if (mySeqNo != null) {
            throw IllegalStateException("Already locked!")
        }
        mySeqNo = zk.create("/lock-") {
            flags = Ephemeral and Sequential
        }.first.let { ZKPaths.extractSequentialSuffix(it)!! }
        val seqNo = mySeqNo!!

        val lockWait: Channel<Unit> = Channel(1)
        while (true) {
            val seqNos = zk.getChildren("/").first
                .map { ZKPaths.extractSequentialSuffix(it)!! }
                .sorted()

            if (seqNo == seqNos[0]) {
                break
            } else {
                val nextSeqNo = seqNos[1]
                val (exists, _) = zk.exists("/lock-$nextSeqNo") {
                    watchers += { _, _, _ -> lockWait.send(Unit) }
                }
                if (!exists) {
                    continue
                } else {
                    if (nextSeqNo == mySeqNo) {
                        lockWait.send(Unit)
                    }
                    lockWait.receive()
                }
            }
        }
    }

    suspend fun unlock() {
        zk.delete("/lock-${mySeqNo}")
    }
}
