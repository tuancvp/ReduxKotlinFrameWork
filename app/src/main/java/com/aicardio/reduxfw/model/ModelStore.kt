package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.IAction

class ModelStore {
    val actionToModel = HashMap<IAction, ArrayList<Model> >()

    fun store(model: Model) {
        model.actionStore.actionToReducer.forEach {
            if (!actionToModel.contains(it.key)) {
                actionToModel.put(it.key, ArrayList())
            }
            actionToModel[it.key]?.add(model)
        }
    }

    fun getModelsByAction(a: IAction): ArrayList<Model> {
        return actionToModel.get(a)?.let { it } ?: ArrayList()
    }

}
