import java.io.File
import java.util.concurrent.Executors
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.InputStream
import java.util.function.Consumer





class ModuleAnalyzer() {
    fun analyze(path: String) {

        File("$path/pom.xml").forEachLine { line -> println(line) }


        val isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows")
        val builder = ProcessBuilder()
        if (isWindows) {
            builder.command("cmd.exe", "/c", "dir")
        } else {
            builder.command("sh", "-c", "mvn dependency:tree > deps")
        }
        builder.directory(File("/home/jwin/IdeaProjects/spring-boot-kafka"))
        val process = builder.start()
        val streamGobbler = StreamGobbler(process.inputStream, Consumer { println(it) })
        Executors.newSingleThreadExecutor().submit(streamGobbler)
        val exitCode = process.waitFor()
        assert(exitCode == 0)


        File("/home/jwin/IdeaProjects/spring-boot-kafka/deps").forEachLine { line -> println(line) }
    }

}

internal class StreamGobbler(private val inputStream: InputStream, private val consumer: Consumer<String>) : Runnable {

    override fun run() {
        BufferedReader(InputStreamReader(inputStream)).lines()
                .forEach(consumer)
    }
}

fun main(args : Array<String>) {
    ModuleAnalyzer().analyze(args[0])
}
