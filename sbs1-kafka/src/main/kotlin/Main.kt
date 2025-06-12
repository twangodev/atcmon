

import SBS1Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import listenToDump1090
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

private const val KAFKA_TOPIC = "flight.state.snapshot"

private fun listenToDump1090(
    host: String,
    port: Int,
    state: ConcurrentHashMap<String, Aircraft>
) {
    Socket(host, port).use { socket ->
        val scope = CoroutineScope(Dispatchers.IO)
        socket
            .getInputStream()
            .bufferedReader()
            .forEachLine { line ->
                scope.launch {
                    SBS1Event.fromString(line)?.modifyState(state)
                }
            }
    }
}

fun main() {

    val dump1090Host = System.getenv("DUMP1090_HOST") ?: "localhost"
    val dump1090Port = System.getenv("DUMP1090_PORT")?.toInt() ?: 30003
    val brokers = System.getenv("KAFKA_BROKER") ?: "localhost:9092"

    val kafkaProps = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to brokers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
    )

    val producer = KafkaProducer<String, String>(kafkaProps)
    val state = ConcurrentHashMap<String, Aircraft>()

    while (true) {
        try {
            listenToDump1090(dump1090Host, dump1090Port, state)
        } catch (e: Exception) {
            println("Error listening to dump1090: ${e.message}")
            e.printStackTrace()
        }
    }

}

