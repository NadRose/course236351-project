package zookeeper.kotlin.zookeeper


interface ZooKeeperKt
    : ZooKeeperCreator, ZooKeeperNamespacer, ZooKeeperChildrenGetter,
    ZooKeeperDeletor, ZooKeeperExistenceChecker {
    override suspend fun usingNamespace(namespace: Path): ZooKeeperKt =
        NamespaceDecorator.make(this, namespace)
}
