package nl.rsdt.japp.service.cloud.data

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-8-2016
 * Description...
 */
class UpdateInfo {

    var type: String? = null

    var action: String? = null

    companion object {

        val ACTION_NEW = "new"

        val ACTION_UPDATE = "update"

        fun parse(data: Map<String, String>): UpdateInfo {
            val info = UpdateInfo()
            info.type = data["type"]
            info.action = data["action"]
            return info
        }
    }

}
