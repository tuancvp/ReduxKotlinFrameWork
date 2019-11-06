package com.aicardio.reduxfw.model

import com.aicardio.reduxfw.iface.ISubscriber

class SubcriberStore {
    val keyToSubcriber = HashMap<String, ArrayList<ISubscriber> >()

    fun store(keys: ArrayList<String>, subscriber: ISubscriber) {
        keys.forEach {
            if (!keyToSubcriber.contains(it)) {
                keyToSubcriber.put(it, ArrayList())
            }
            keyToSubcriber[it]?.add(subscriber)
        }
    }

    fun getSubscribersByKeys(changedKeys: ArrayList<String>): ArrayList<ISubscriber> {
        val ret = ArrayList<ISubscriber>()
        changedKeys.forEach {
            val sub = keyToSubcriber.get(it)
            sub?.let { ret.addAll(it) }
        }
        return ret
    }

}
