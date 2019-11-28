import kotlinx.coroutines.*
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import kotlin.system.measureTimeMillis

const val URI = "http://www.ya.ru"
val isAsyncMode = false
val isBlockMode = true

/*
block 2006
async 1558
stub 1047
 */

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        val one = async { launchClient(1) }
        val two = async { launchClient(2) }
        val three = async { launchClient(3) }
        val four = async { launchClient(4) }
        val five = async { launchClient(5) }
        one.await()
        two.await()
        three.await()
        four.await()
        five.await()
    }
    println("Completed in $time ms")
}

suspend fun launchClient(step: Int) {
    println("step $step before")
    launchClient()
    println("step $step after")
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
    val request = HttpGet(URI)
    client.execute(request)
    client.close()
}

private fun launchClientBlock() {
    val client = HttpClients.createDefault()
    val httpGet = HttpGet(URI)
    client.execute(httpGet)
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