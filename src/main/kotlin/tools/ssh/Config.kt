package tools.ssh

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import tools.ssh.cluster.Cluster
import tools.ssh.cluster.Service
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by sundl on 2017/6/23.
 */
data class Config(val cluster: Cluster, val services: Array<Service>) {

}

fun load(): Config {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    val path = System.getProperty("user.dir")
    return Files.newBufferedReader(Paths.get(path, "config.yaml")).use {
        mapper.readValue(it, Config::class.java)
    }
}