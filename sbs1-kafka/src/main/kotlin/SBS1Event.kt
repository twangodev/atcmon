

import java.util.concurrent.ConcurrentHashMap

enum class SBSMessageType {

}

data class SBS1Event(
    val msgType: SBSMessageType,
) {

    companion object {
        fun fromString(line: String): SBS1Event? {
            println(line)
            return null
        }
    }

    fun modifyState(state: ConcurrentHashMap<String, Aircraft>) {

    }


}

