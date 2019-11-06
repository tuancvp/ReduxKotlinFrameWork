package com.aicardio.reduxfw.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.aicardio.reduxfw.iface.ISubscriber
import org.json.JSONObject

class TextViewSubscriber @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr), ISubscriber {

    var key : String = ""

    override fun onDataChanged(data: JSONObject, changedKeys: ArrayList<String>) {
        if (changedKeys.contains(key)) {
            text = data.getString(key)
        }
    }

    fun setListenKeys(s: String) {
        key = s
    }
}