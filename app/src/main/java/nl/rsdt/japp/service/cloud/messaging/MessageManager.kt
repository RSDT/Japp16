package nl.rsdt.japp.service.cloud.messaging

import nl.rsdt.japp.service.cloud.RemoteMessage
import nl.rsdt.japp.service.cloud.data.MessageType
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-8-2016
 * Handles the messages for Japp
 */
class MessageManager {

    /**
     * The list of listeners.
     */
    private val listeners = ArrayList<UpdateMessageListener>()

    /**
     * Adds a listener for when a UpdateMessage is received.
     */
    fun add(listener: UpdateMessageListener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener for when a UpdateMessage is received.
     */
    fun remove(listener: UpdateMessageListener) {
        listeners.remove(listener)
    }

    /**
     * Gets invoked when the service received a message with data.
     */
    fun onMessage(message: RemoteMessage) {
        if (message.data != null && !message.data.isEmpty()) {
            val data = message.data
            val type = data["type"] ?: ""
            if (type == MessageType.NOTICE) {
                if (listeners.size > 0) {
                    val info = NoticeInfo.parse(data)
                    var listener: UpdateMessageListener?
                    for (i in listeners.indices) {
                        listener = listeners[i]
                        listener.onNoticeMessageReceived(info)
                    }
                }
            } else if (type.startsWith("vos") || type == "foto" || type == "sc") {
                if (listeners.size > 0) {
                    val info = UpdateInfo.parse(data)
                    if (info.type != null && info.action != null) {
                        var listener: UpdateMessageListener?
                        for (i in listeners.indices) {
                            listener = listeners[i]

                            listener.onUpdateMessageReceived(info)
                        }
                    }
                }
            }
        }
    }


    /**
     * Defines a listener for when a UpdateMessage is received.
     */
    interface UpdateMessageListener {

        /**
         * Gets invoked when a UpdateMessage is received.
         *
         * @param info The information about the update.
         */
        fun onUpdateMessageReceived(info: UpdateInfo?)

        fun onNoticeMessageReceived(info: NoticeInfo?)
    }

}
