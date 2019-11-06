package com.aicardio.reduxfw.iface

interface IModel {
    abstract fun add_action(a: IAction, r: IReducer)
    abstract fun add_model(key: String, model: IModel)
    abstract fun add_subscriber(keys: Array<String>, subscriber: ISubscriber)
    abstract fun perform_action(a: IAction)
}
