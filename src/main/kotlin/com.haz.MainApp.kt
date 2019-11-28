import kotlinx.coroutines.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

suspend fun doSomethingUsefulOne(): Int {
    println("doSomethingUsefulOne before")
//    launchClient()
    delay(1000L) // pretend we are doing something useful here
    println("doSomethingUsefulOne after")
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    println("doSomethingUsefulTwo before")
//    launchClient()
    delay(1000L) // pretend we are doing something useful here, too
    println("doSomethingUsefulTwo after")
    return 29
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
//    println("client finished")
}