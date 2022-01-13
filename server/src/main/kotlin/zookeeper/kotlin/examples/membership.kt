package zookeeper.kotlin.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import zookeeper.kotlin.zookeeper.ZChildren
import zookeeper.kotlin.zookeeper.ZooKeeperKt
import zookeeper.kotlin.createflags.Ephemeral

fun main(args: Array<String>) = mainWith(args) { _, zk ->
    val mem = Membership.make(zk, args[0])

    val chan = Channel<ZChildren>()
    mem.onChange = {
        chan.send(mem.queryMembers())
    }
    val task = launch {
        for (members in chan) {
            println("Members: ${members.joinToString(", ")}")
        }
    }

    chan.send(mem.queryMembers())
    mem.join(args[1])
    task.join()
}

class Membership private constructor(private val zk: ZooKeeperKt, val groupName: String) {
    var _id: String? = null
    val id: String get() = _id!!

    var onChange: (suspend () -> Unit)? = null

    suspend fun join(id: String) {
        val (_, stat) = zk.create {
            path = "/$id"
            flags = Ephemeral
        }
        _id = id
    }

    suspend fun queryMembers(): List<String> = zk.getChildren("/") {
        watchers += this@Membership.onChange?.let { { _, _, _ -> it() } }
    }.first

    suspend fun leave() {
        zk.delete("/$id")
    }

    companion object {
        suspend fun make(zk: ZooKeeperKt, groupName: String): Membership {
            val zk = zk.usingNamespace("/membership")
                .usingNamespace("/$groupName")
            return Membership(zk, groupName)
        }
    }
}