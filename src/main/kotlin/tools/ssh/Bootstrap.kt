package tools.ssh

import org.beetl.core.Configuration
import tools.ssh.SSHClient;
import org.beetl.core.GroupTemplate
import org.beetl.core.resource.FileResourceLoader
import org.beetl.core.resource.StringTemplateResourceLoader
import tools.ssh.cluster.Deployer
import tools.ssh.cluster.preparePkg

/**
 * Created by sundl on 2017/6/22.
 */
fun main(args: Array<String>) {
    val config = load()
    println(config)

    val deploy = Deployer(config.cluster)
    config.services.forEach {
        deploy.upload(it)
    }

}

fun template() {
    val resourceLoader = FileResourceLoader()
    val cfg = Configuration.defaultConfiguration()
    val gt = GroupTemplate(resourceLoader, cfg)
    val t = gt.getTemplate("test.template")
    t.binding("name", "beetl")
    val str = t.render()

    println(str)
}