import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.*
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import kotlin.system.measureTimeMillis

enum class Mode {
    stub,
    block,
    async,
    ktor
}

val mode = Mode.async
const val maxSteps = 20
const val URI = "http://www.ya.ru"

/*
for 20 steps

stub 1046 ms
block 4735 ms
async 2109 ms
ktor 2500 ms(

 */

fun main() = runBlocking {

    val time = measureTimeMillis {
        (1..maxSteps).map { async { launchClient(it) } }.forEach { t -> t.await() }
    }
    println("Completed in $time ms")
}

suspend fun launchClient(step: Int) {
    println("step $step before")
    launchClient()
    println("step $step after")
}

suspend fun launchClient() {
    when (mode) {
        Mode.stub -> {
            delay(1000L)
        }
        Mode.block -> {
            launchClientBlock()
        }
        Mode.async -> {
            launchClientAsync()
        }
        Mode.ktor -> {
            launchClientKtor()
        }
    }
}

private suspend fun launchClientAsync() {
    val client = HttpAsyncClients.createDefault()
    client.start()
    val request = HttpGet(URI)
    client.execute(request)
    client.close()
}

private fun launchClientBlock() {
    val client = HttpClients.createDefault()
    val httpGet = HttpGet(URI)
    client.execute(httpGet)
}

suspend fun launchClientKtor() {
    val client = HttpClient()
    client.get<String>(URI)
    client.close()
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