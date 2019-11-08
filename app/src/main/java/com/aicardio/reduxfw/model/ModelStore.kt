package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.IAction

class ModelStore {
    val actionToModel = HashMap<String, ArrayList<Model> >()

    fun store(model: Model) {
        model.actionStore.actionToReducer.forEach {
            if (!actionToModel.contains(it.key)) {
                actionToModel.put(it.key, ArrayList())
            }
            actionToModel[it.key]?.add(model)
        }
    }

    fun getModelsByAction(a: IAction): ArrayList<Model> {
        val key = a.javaClass.name
        return actionToModel.get(key)?.let { it } ?: ArrayList()
    }

}
