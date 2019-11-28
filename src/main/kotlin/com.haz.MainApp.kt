import kotlinx.coroutines.*
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

val isAsyncMode = false
val isBlockMode = false

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
    launchClient()
    println("doSomethingUsefulOne after")
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    println("doSomethingUsefulTwo before")
    launchClient()
    println("doSomethingUsefulTwo after")
    return 29
}

suspend fun launchClient() {
    when {
        isAsyncMode -> {
            launchClientAsync()
        }
        isBlockMode -> {
            launchClientBlock()
        }
        else -> {
            delay(1000L) // pretend we are doing something useful here
        }
    }
}

private suspend fun launchClientAsync() {
    val client = HttpAsyncClients.createDefault()
    client.start()
    val request = HttpGet("http://www.ya.ru")
    val future = client.execute(request)
    client.close()
}

private suspend fun launchClientBlock() {
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

}

suspend fun HttpAsyncClient.execute(request: HttpUriRequest): HttpResponse {
    return suspendCancellableCoroutine { cont: CancellableContinuation<HttpResponse> ->
        val future = this.execute(request, object : FutureCallback<HttpResponse> {
            override fun completed(result: HttpResponse) {
                cont.resumeWith(Result.success(result))
            }

            override fun cancelled() {
                if (cont.isCancelled) return
                cont.resumeWith(Result.failure(CancellationException("Cancelled")))
            }

            override fun failed(ex: Exception) {
                cont.resumeWith(Result.failure(ex))
            }
        })

        cont.cancelFutureOnCancellation(future);
        Unit
    }
}