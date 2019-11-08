package com.aicardio.reduxfw.iface

import com.aicardio.reduxfw.model.Model
import org.json.JSONObject

interface IReducer {
    // compute changes from action, possibly perform new actions
    abstract fun dispatch(model: Model, action: IAction) : ArrayList<String> // return list of changed keys
}
