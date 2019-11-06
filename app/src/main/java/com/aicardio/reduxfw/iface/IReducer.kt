package com.aicardio.reduxfw.iface

import org.json.JSONObject

interface IReducer {
    // compute changes from action, possibly perform new actions
    abstract fun dispatch(data: JSONObject, action: IAction) : ArrayList<String> // return list of changed keys
}
