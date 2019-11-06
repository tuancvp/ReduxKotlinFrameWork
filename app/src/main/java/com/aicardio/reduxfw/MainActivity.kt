package com.aicardio.reduxfw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer
import com.aicardio.reduxfw.iface.ISubscriber
import com.aicardio.reduxfw.model.Model
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    val model = Model()
    val subModel = Model()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickAction = ClickAction()
        val clickReducer = ClickReducer()
//        val valueSubscriber = ValueSubscriber(this)

        val initAction = InitValueAction()
        val initReducer = InitValueReducer()

        model.add_action(clickAction, clickReducer)
        model.add_action(initAction, initReducer)

        text.setListenKeys("value")
        model.add_subscriber(arrayListOf("value"), text)

        val subClickAction = SubClickAction()
        val subClickReducer = SubClickReducer()
        subtext.setListenKeys("value")
//        val subTextSubscriber = SubTextSubscriber(this)
        subModel.add_action(subClickAction, subClickReducer)
        subModel.add_subscriber(arrayListOf("value"), subtext)

        button.setOnClickListener {
            model.perform_action(clickAction)
            subModel.perform_action(subClickAction)
        }


        model.add_model("subtext", subModel)
        model.perform_action(initAction)
    }

    class SubClickAction : IAction {

    }

//    class SubTextSubscriber(val activity: MainActivity) : ISubscriber {
//        override fun onDataChanged(data: JSONObject, changedKeys: ArrayList<String>) {
//            if (changedKeys.contains("value")) {
//                activity.subtext.text = data.getString("value")
//            }
//        }
//
//    }

    class SubClickReducer : IReducer {
        override fun dispatch(component_data: JSONObject, action: IAction): ArrayList<String> {
            if (action is SubClickAction) {
                if (component_data.has("value")) {
                    val value = component_data.getString("value")
                    component_data.put("value", if (value != "done") "done" else "not done")
                } else {
                    component_data.put("value","init value")
                }
                return arrayListOf("value")
            }
            return ArrayList()
        }
    }

    class InitValueReducer : IReducer {
        override fun dispatch(data: JSONObject, action: IAction): ArrayList<String> {
            if (action is InitValueAction) {
                data.put("value", "Initialized")
                return arrayListOf("value")
            }
            return ArrayList()
        }
    }

    class ValueSubscriber(val activity: MainActivity) : ISubscriber {
        override fun onDataChanged(data: JSONObject, changedKeys: ArrayList<String>) {
            if (changedKeys.contains("value")) {
                activity.text.text = data.getString("value")
            }
        }

    }

    class ClickReducer : IReducer {
        override fun dispatch(data: JSONObject, action: IAction): ArrayList<String> {
            if (action is ClickAction) {
                data.put("value", "Done")
                return arrayListOf("value")
            }
            return ArrayList()
        }

    }

    class ClickAction : IAction {

    }

    class InitValueAction : IAction {

    }
}
