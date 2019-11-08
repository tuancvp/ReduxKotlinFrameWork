package com.aicardio.reduxfw.model

import android.os.AsyncTask
import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer
import com.aicardio.reduxfw.iface.ISubscriber
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Model() {
    val actionStore = ActionStore()
    val subscriberStore = SubcriberStore()
//    val modelStore = ModelStore()
    val currentChangeKeys = Stack< ArrayList<String> > ()

    private val data = JSONObject()

    fun add_action(a: IAction, r: IReducer) {
        actionStore.store(a, r)
    }

    fun add_model(key: String, model: Model) {
//        modelStore.store(model)
        data.put(key, model.data)
    }
    fun startChange() {
        currentChangeKeys.push(ArrayList())

    }

    fun <T> changeKey(key: String, value: T) {
        currentChangeKeys.peek().add(key)
        data.put(key, value)
    }

    fun stopChange(): ArrayList<String> {
        val top = currentChangeKeys.pop()
        return top
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValueByKey(key: String) : T? {
        val value : T? = data.get(key) as? T
        return value
    }

    fun add_subscriber(keys: ArrayList<String>, subscriber: ISubscriber) {
        subscriberStore.store(keys, subscriber)
    }

    fun add_subscriber(key: String, subscriber: ISubscriber) {
        subscriberStore.store(arrayListOf(key), subscriber)
    }

    fun perform_action(a: IAction) {
        val reducers = actionStore.getReducers(a)
        reducers.forEach {
            val changedKeys = it.dispatch(this, a)
            subscriberStore.getSubscribersByKeys(changedKeys).forEach {
                it.onDataChanged(data, changedKeys)
            }
        }

    }

    fun perform_async_action(a: IAction) {
        val reducers = actionStore.getReducers(a)
        val asyncTask = ModelAsyncTask(a, reducers, this)
        asyncTask.execute()

    }

    class ModelAsyncTask(
        val action: IAction,
        val reducers: java.util.ArrayList<IReducer>,
        val model: Model
    ) : AsyncTask<Void, Void, ArrayList<String>>() {
        override fun doInBackground(vararg p0: Void?): ArrayList<String> {
            val changes = ArrayList<String>()
            reducers.forEach {
                val changedKeys = it.dispatch(model, action)
                changes.addAll(changedKeys)
            }
            return changes
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            result?.let { changes ->
                model.subscriberStore.getSubscribersByKeys(changes).forEach { subscriber ->
                    subscriber.onDataChanged(model.data, changes)
                }
            }
        }
    }


    fun hasValue(key: String): Boolean {
        return data.has(key)
    }
}