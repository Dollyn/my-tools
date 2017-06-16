package tools.ssh

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.TransportException
import org.slf4j.LoggerFactory
import net.schmizz.sshj.common.DisconnectReason
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.util.*


/**
 * test
 * Created by sundl on 2017/6/16.
 */

val log = LoggerFactory.getLogger("tools")

fun main(args: Array<String>) {
//    val client = connect()
//    val session = client.startSession()
//    val cmd = session.exec("ping -c 1 kg1");
//    println(cmd.inputStream.reader().readText())
//
//    cmd.join(5, TimeUnit.SECONDS)
//    println("\n** exit status: " + cmd.exitStatus)

    generatePublicKey()
}

fun connect(): SSHClient {
    var client = SSHClient()
    try {
        client.connect("kg1")
    } catch (e: TransportException) {
        if (e.disconnectReason === DisconnectReason.HOST_KEY_NOT_VERIFIABLE) {
            val msg = e.message
            val split = msg!!.split("`".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val vc = split[3]
            client = SSHClient()
            client.addHostKeyVerifier(vc)
            client.connect("kg1")
        } else {
            throw e
        }
    }
    client.authPassword("root", "fdDCT54ifdDCT54i")
    return client
}

/**
 * create public key pair, and then we can use the 'authPublicKey()' way instead of username-password
 * TODO: make the algorithm and the key size configurable.
 */
fun generatePublicKey() {
    val generator = KeyPairGenerator.getInstance("RSA")
    val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
    generator.initialize(1024, random)
    val pair = generator.genKeyPair()

    println(Base64.getEncoder().encodeToString(pair.private.encoded))
    println(Base64.getEncoder().encodeToString(pair.public.encoded))
}

