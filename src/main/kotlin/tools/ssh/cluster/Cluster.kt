package tools.ssh.cluster

/**
 * Created by sundl on 2017/6/23.
 */
class Cluster (val name: String, val nodes: Array<Node>){

    fun getNode(name: String): Node? {
        nodes.forEach {
            if (it.name == name) {
                return it
            }
        }
        return null
    }

}

data class Node(val name: String, val host: String)
