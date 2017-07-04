package tools.ssh

import com.jcraft.jsch.*
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.TransportException
import org.slf4j.LoggerFactory
import net.schmizz.sshj.common.DisconnectReason
import net.schmizz.sshj.common.KeyType
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.util.*
import java.io.IOException
import java.nio.file.Files


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

//    generatePublicKey()

    //jsch()
    jschConnect()

    //connect()
}

fun jsch() {
    val jsch = JSch()
    try {

        val keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 1024)
        val os = ByteArrayOutputStream()

        keyPair.writePrivateKey(os)
        keyPair.writePrivateKey(os, "123456".toByteArray())
        keyPair.writePublicKey(os, "my-tools generated public key")

        println(os.toString())
        keyPair.dispose()
    } catch (ex: Exception) {
        log.error(ex.toString(), ex)
    }

}

fun jschConnect() {
    val jsch = JSch()
    jsch.setKnownHosts("C:\\Users\\sundl\\.ssh\\known_hosts")
    jsch.addIdentity("C:\\Users\\sundl\\.ssh\\id_rsa_mytools")

    val session = jsch.getSession("root", "kg1")
    //session.setPassword("fdDCT54ifdDCT54i")

    val config = java.util.Properties()
    //config.put("StrictHostKeyChecking", "no")
    session.setConfig(config)

    try {
        session.connect()
    } catch (e: Exception) {
        log.error("", e)
    }

    val execChannel = session.openChannel("exec") as ChannelExec
    execChannel.setCommand("ls")
    execChannel.outputStream = System.out
    execChannel.setErrStream(System.err)
    try {
        execChannel.connect()
        execChannel.inputStream.bufferedReader().lines().forEach {
            println(it)
        }
    } finally {
        execChannel.disconnect()
    }

//    var sftpChannel: ChannelSftp? = null
//    try {
//        sftpChannel = session.openChannel("sftp") as ChannelSftp
//        sftpChannel!!.connect()
//
////        // download
////        val inputStream = sftpChannel!!.get("/root/.ssh")
////        Files.copy(inputStream, localPath)
//
//        val keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 1024)
//        val os = ByteArrayOutputStream()
//
//        keyPair.writePrivateKey(os)
//        keyPair.writePrivateKey(os, "123456".toByteArray())
//
//
//        // upload
//        val outputStream = sftpChannel!!.put("/root/.ssh/authorized_keys", ChannelSftp.APPEND)
//        //Files.copy(locaPathl, outputStream)
//        keyPair.writePublicKey(outputStream, "my-tools generated public key")
//
//        println(os.toString())
//        keyPair.dispose()
//
//    } catch (ex: SftpException) {
//        throw IOException(ex)
//    } catch (ex: JSchE    xception) {
//        throw IOException(ex)
//    } finally {
//        if (sftpChannel != null) {
//            sftpChannel!!.disconnect()
//        }
//    }

    session.disconnect()
}



fun connect(): SSHClient {
    var client = SSHClient()
    try {
        client.connect("kg1")
    } catch (e: TransportException) {
        if (e.disconnectReason === DisconnectReason.HOST_KEY_NOT_VERIFIABLE) {
            val msg = e.message
            println(msg)
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

