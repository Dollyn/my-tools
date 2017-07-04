package tools.ssh.cluster

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.nio.charset.Charset

/**
 * Created by sundl on 2017/6/23.
 */
class Service(val installPath: String, val pkgPath: String,
                   val configFiles: Array<String> = arrayOf(),
                   val nodeNames: Array<String> = arrayOf()) {

}

fun preparePkg(service: Service, node: Node) {
    val fin = Files.newInputStream(Paths.get(service.pkgPath))
    val input = BufferedInputStream(fin)
    val out = Files.newOutputStream(Paths.get("archive.tar"))
    val gzIn = GzipCompressorInputStream(input)

    gzIn.use {
        val tarIn = TarArchiveInputStream(gzIn)
        tarIn.use {
            var next = tarIn.nextTarEntry
            while (next != null) {
                if (next.name == "orientdb-community-tp3-3.0.0m1/config/hazelcast.xml") {
                    val bytes = tarIn.readBytes(next.size.toInt())
                    println(bytes.toString(Charset.defaultCharset()))
                }
                next = tarIn.nextTarEntry
            }
        }
    }

    //val buffer = ByteArray(buffersize)
    //var n = 0
    //while (-1 != (n = gzIn.read(buffer))) {
//        out.write(buffer, 0, n)
//    }
//    out.close()
//    gzIn.close()

}

