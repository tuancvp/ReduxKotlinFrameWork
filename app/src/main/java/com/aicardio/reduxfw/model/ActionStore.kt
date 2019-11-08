package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer

class ActionStore {
    val actionToReducer = HashMap<String, ArrayList<IReducer> >()
    fun store(a: IAction, r: IReducer) {
        val key = a.javaClass.name
        if (!actionToReducer.contains(key)) {
            actionToReducer.put(key, ArrayList())
        }
        actionToReducer[key]?.add(r)
    }

    fun getReducers(a: IAction): ArrayList<IReducer> {
        val key = a.javaClass.name
        return actionToReducer.get(key)?.let { it } ?: ArrayList()
    }

}
