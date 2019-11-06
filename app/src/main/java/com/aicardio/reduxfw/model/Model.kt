package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer
import com.aicardio.reduxfw.iface.ISubscriber
import org.json.JSONObject

class Model() {
    val actionStore = ActionStore()
    val subscriberStore = SubcriberStore()
    val modelStore = ModelStore()
    val data = JSONObject()

    fun add_action(a: IAction, r: IReducer) {
        actionStore.store(a, r)
    }

    fun add_model(key: String, model: Model) {
//        modelStore.store(model)
        data.put(key, model.data)
    }

    fun add_subscriber(keys: ArrayList<String>, subscriber: ISubscriber) {
        subscriberStore.store(keys, subscriber)
    }

    fun perform_action(a: IAction) {
        val reducers = actionStore.getReducers(a)
        reducers.forEach {
            val changedKeys = it.dispatch(data, a)
            subscriberStore.getSubscribersByKeys(changedKeys).forEach {
                it.onDataChanged(data, changedKeys)
            }
        }
//        modelStore.getModelsByAction(a).forEach {
//            it.perform_action(a)
//        }
    }
}