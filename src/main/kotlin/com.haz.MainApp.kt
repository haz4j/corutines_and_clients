
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader
import java.io.InputStreamReader


fun main() {
    val launch = GlobalScope.launch {
        // launch a new coroutine in background and continue
        launchClient()
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    Thread.sleep(5000L) // block main thread for 2 seconds to keep JVM alive

    launchClient()
}

private fun launchClient() {
    val client = HttpClients.createDefault()
    val httpGet = HttpGet("http://www.ya.ru")
    val response = client.execute(httpGet)
    val rd = BufferedReader(InputStreamReader(
            response.entity.content))

    val textView = StringBuilder()
    var line: String? = ""
    while (rd.readLine().also { line = it } != null) {
        textView.append(line)
    }

//    println(textView.toString())
    println("client finished")
}