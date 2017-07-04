package tools.ssh

import com.jcraft.jsch.*
import org.apache.commons.io.IOUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by sundl on 2017/6/22.
 */
class SSHClient(val host: String, val port: Int = 22, val user: String = "root", val password: String = ""): Closeable {

    val id = host + port
    var connected = false
    val sshCient = JSch()
    val session = sshCient.getSession(user, host)!!

    init {
        connect()
    }

    fun connect() {
        jschConnect()
        connected = true
    }

    private fun jschConnect() {
        // TODO config
        sshCient.setKnownHosts("C:\\Users\\sundl\\.ssh\\known_hosts")
        sshCient.addIdentity("C:\\Users\\sundl\\.ssh\\id_rsa_mytools")

        // TODO infer user password or public key
        //session.setPassword("fdDCT54ifdDCT54i")

        val config = java.util.Properties()
        // TODO only first time connect
        //config.put("StrictHostKeyChecking", "no")
        session.setConfig(config)

        try {
            session.connect()
        } catch (e: Exception) {
            log.error("", e)
        }
    }

    fun exec(cmd: String): String {
        val execChannel = session.openChannel("exec") as ChannelExec
        execChannel.setCommand("ls")
        execChannel.outputStream = System.out
        execChannel.setErrStream(System.err)
        try {
            execChannel.connect()
            return execChannel.inputStream.bufferedReader().readText()
        } finally {
            execChannel.disconnect()
        }
    }

    /**
     *
     * @param path file path that the content will be appended
     * @param content content to append
     */
    fun append(path: String, content: String) {
        var sftpChannel: ChannelSftp? = null
        try {
            sftpChannel = session.openChannel("sftp") as ChannelSftp
            sftpChannel!!.connect()

            val keyPair = KeyPair.genKeyPair(sshCient, KeyPair.RSA, 1024)
            val os = ByteArrayOutputStream()

            keyPair.writePrivateKey(os)
            keyPair.writePrivateKey(os, "123456".toByteArray())

            // upload
            val outputStream = sftpChannel!!.put(path, ChannelSftp.APPEND)
            keyPair.writePublicKey(outputStream, "my-tools generated public key")

            println(os.toString())
            keyPair.dispose()
        } catch (ex: SftpException) {
            throw IOException(ex)
        } catch (ex: JSchException) {
            throw IOException(ex)
        } finally {
            if (sftpChannel != null) {
                sftpChannel!!.disconnect()
            }
        }
    }

    fun upload(src: String, target: String) {
        val channel = session.openChannel("sftp") as ChannelSftp
        try {
            channel.connect()
            //channel.get()
            channel.mkdir(target)
            val inStream = Files.newInputStream(Paths.get(src))
            inStream.use {
                channel.put(inStream, target + "/" + Paths.get(src).fileName)
            }
        } finally {
            channel.disconnect()
        }
    }

    override fun close() {
        session.disconnect()
    }

}


