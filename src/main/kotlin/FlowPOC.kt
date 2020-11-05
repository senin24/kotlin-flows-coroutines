import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/** @author Pavel_Senin */
class FlowPOC {

}

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(92)
        println("Emitting $i")
        emit(i)
    }
}
//
//fun main() = runBlocking<Unit> {
//    withTimeoutOrNull(250) { // Timeout after 250ms
//        simple().collect { value -> println(value) }
//    }
//    println("Done")
//}



suspend fun performRequest(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

fun main() = runBlocking<Unit> {
    (1..3).asFlow() // a flow of requests
        .transform { request ->
            emit("Making request $request")
            emit(performRequest(request))
        }
        .buffer(3)
        .collect { response -> println(response) }
}