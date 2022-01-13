package zookeeper.kotlin.createflags

import zookeeper.kotlin.zookeeper.CreateFlags

val Persistent = CreateFlags.Persistent
val Ephemeral = CreateFlags.Ephemeral
val Container = CreateFlags.Container
val Sequential = CreateFlags.Sequential
typealias TTL = CreateFlags.TTL