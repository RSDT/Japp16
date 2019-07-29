package nl.rsdt.japp.service.cloud.data

import nl.rsdt.japp.R

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 11-9-2016
 * Description...
 */
class NoticeInfo {

    var title: String? = null
        protected set

    var body: String? = null
        protected set

    var iconName: String? = null
        protected set

    val drawable: Int
        get() = parseDrawable(iconName)

    companion object {

        fun parse(data: Map<String, String>): NoticeInfo {
            val buffer = NoticeInfo()
            buffer.title = data["title"]
            buffer.body = data["body"]
            buffer.iconName = data["icon"]
            return buffer
        }

        fun parseDrawable(icon: String?): Int {
            if (icon == null) return R.drawable.ic_info_black_48dp
            when (icon) {
                "info" -> return R.drawable.ic_info_black_48dp
                "belangrijk" -> return R.drawable.ic_priority_high_black_48dp
                else -> return R.drawable.ic_info_black_48dp
            }
        }
    }

}
