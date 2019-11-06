package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer

class ActionStore {
    val actionToReducer = HashMap<IAction, ArrayList<IReducer> >()
    fun store(a: IAction, r: IReducer) {
        if (!actionToReducer.contains(a)) {
            actionToReducer.put(a, ArrayList())
        }
        actionToReducer[a]?.add(r)
    }

    fun getReducers(a: IAction): ArrayList<IReducer> {
        return actionToReducer.get(a)?.let { it } ?: ArrayList()
    }

}
