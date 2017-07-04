package tools.ssh.cluster

import org.slf4j.LoggerFactory
import tools.ssh.SSHClient

/**
 * Created by sundl on 2017/7/3.
 */
class Deployer(val cluster: Cluster) {

    companion object {
        val log = LoggerFactory.getLogger(javaClass)
    }

    val clients = hashMapOf<String, SSHClient>()

    fun upload(service: Service) {
        service.nodeNames.forEach {
            val node = cluster.getNode(it)
            if (node != null) {
                val client = getClient(node.name)
                client!!.upload(service.pkgPath, service.installPath)
            }
        }
    }

    fun getClient(node: String): SSHClient? {
        var client = clients.get(node)
        if (client == null) {
            val n = cluster.getNode(node)
            if (n != null) {
                log.info("create ssh client for $n ")
                client = SSHClient(n.host)
                clients.put(node, client)
            }
        }
        return client
    }

}
