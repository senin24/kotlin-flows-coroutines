import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/** @author Pavel_Senin */
class FlowGeoCode {

    fun recordsFromXmlAndComparedWithCache(): Flow<Record> = flow {
        (1..10)
            .map { i: Int -> Record(i, "name of $i") } //FromXml
            .filter { record -> record.id % 2 == 0 } //ComparedWithCache
            .forEach { record ->
                emit(record)
//                run {
//                    if (record.id == 2 || record.id == 4) {
//                        run {
//                            val (x, y) = geoCoding(record.streetName)
//                            emit(record.copy(x = x, y = y))
//                        }
//                    } else {
//                        emit(record)
//                    }
//                }
            }
    }

    private suspend fun geoCoding(streetName: String): Pair<Int, Int> {
        delay(5000)
        return Pair(10, 10)
    }

    fun consumeFirstFlow(): Flow<Record> = flow {
        recordsFromXmlAndComparedWithCache().buffer(10)
            .collect { record ->
                if (record.id == 4 || record.id == 6) {
                    run {
                        val (x, y) = geoCoding(record.streetName)
                        emit(record.copy(x = x, y = y))
                    }
                } else {
                    emit(record)
                }
            }
    }

    data class Record(
        val id: Int,
        val streetName: String,
        val x: Int = 0,
        val y: Int = 0,
    )
}

suspend fun main() {
    FlowGeoCodeAcync().recordsFromXmlAndComparedWithCache()
        .buffer(10)
        .collect { value -> println(value) }

}