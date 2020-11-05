import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import mu.KotlinLogging
import kotlin.coroutines.coroutineContext

/** @author Pavel_Senin */
class FlowGeoCodeAcync {

    fun recordsFromXmlAndComparedWithCache(): Flow<Record> = flow {
        (1..100)
            .asFlow()
            .map { i: Int -> Record(i, "name of $i") } //FromXml
            .filter { record -> record.id % 2 == 0 } //ComparedWithCache
            .collect { record ->
                if (record.id % 4 == 0) {
                    val (x, y) = geoCoding(record.streetName)
                    val value = record.copy(x = x, y = y)
                    log("Emitting GEO record: $value")
                    emit(value)
                } else {
                    log("Emitting SIMPLE record: $record")
                    emit(record)
                }
            }
    }

    suspend fun geoCoding(streetName: String): Pair<Int, Int> {
//        Thread.sleep(100)
        return Pair(10, 10)
    }


//    fun consumeFirstFlow(): Flow<Record> = flow {
//        recordsFromXmlAndComparedWithCache().buffer(10)
//            .collect { record ->
//                if (record.id == 4 || record.id == 6) {
//                    run {
//                        val (x, y) = geoCoding(record.streetName)
//                        emit(record.copy(x = x, y = y))
//                    }
//                } else {
//                    emit(record)
//                }
//            }
//    }

    data class Record(
        val id: Int,
        val streetName: String,
        val x: Int = 0,
        val y: Int = 0,
    )
}

suspend fun main() {
    val requestSemaphore = Semaphore(5)

    withContext(coroutineContext) {
        FlowGeoCodeAcync().recordsFromXmlAndComparedWithCache()
//            .buffer(10)
//            .onEach { delay(1000) }
            .collect() { record ->
                launch {
                    requestSemaphore.withPermit {
                        log("Start consume record: $record")
                        delay(1000)
                        log("Finish consume record: $record")
                    }
                }
            }
    }
}

//private val log = KotlinLogging.logger {}

suspend fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")