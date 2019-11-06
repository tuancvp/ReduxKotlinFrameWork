package com.aicardio.reduxfw.iface

import org.json.JSONObject

interface ISubscriber {
    abstract fun onDataChanged(
        data: JSONObject,
        changedKeys: ArrayList<String>
    )

}
