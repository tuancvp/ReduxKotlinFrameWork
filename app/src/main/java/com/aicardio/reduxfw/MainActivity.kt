package com.aicardio.reduxfw

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer
import com.aicardio.reduxfw.iface.ISubscriber
import com.aicardio.reduxfw.model.Model
import com.aicardio.reduxfw.view.ItemChosenListener
import com.aicardio.reduxfw.view.ListItemView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), ItemChosenListener {

    val model = Model()
    val subModel = Model()
    private var permissionGranted = false

    companion object {
        private const val MY_PERMISSIONS_REQUEST_CODE = 1
        const val OUTER_TEXT = "value"
        const val SUB_TEXT = "value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        checkAndRequestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
        )

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
        model.add_model("file_paths", rv_folder_list.model)
        model.perform_action(initAction)

        val context = this
        rv_folder_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rv_folder_list.getMyAdapter()
            setHasFixedSize(true)
        }

        rv_folder_list.model.perform_action(ListItemView.InitAction("/storage/emulated/0/Download"))
        rv_folder_list.itemChosenListener = this

    }

    override fun onItemChosen(file_path: String) {
        subModel.perform_action(SubClickAction(file_path))
    }

    private fun checkAndRequestPermissions(
        thisActivity: AppCompatActivity,
        permissions: Array<String>
    ) {
        // Here, thisActivity is the current activity
        var granted = true
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    thisActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                granted = false
        }

        permissionGranted = if (!granted) {
            // Permission is not granted
            // Should we show an explanation?
            ActivityCompat.requestPermissions(
                thisActivity,
                permissions,
                MY_PERMISSIONS_REQUEST_CODE
            )
            false
        } else {
            true
        }
        Logger.getLogger("MainActivity").warning("Permission granted = $permissionGranted")
    }

    class SubClickAction(val filePath: String = "no value") : IAction {

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
        override fun dispatch(model: Model, action: IAction): ArrayList<String> {
            model.startChange()

            if (action is SubClickAction) {

//                val component_data = model.data
                if (action.filePath == "no value") {
                    if (model.hasValue(SUB_TEXT)) {
                        val value: String? = model.getValueByKey(SUB_TEXT)
                        model.changeKey(SUB_TEXT, if (value != "done") "done" else "not done")
                    } else {
                        model.changeKey(SUB_TEXT, "init value")
                    }
                } else {
                    model.changeKey(SUB_TEXT, action.filePath)
                }

            }
            return model.stopChange()
        }
    }

    class InitValueReducer : IReducer {
        override fun dispatch(model: Model, action: IAction): ArrayList<String> {
            model.startChange()

            if (action is InitValueAction) {
                model.changeKey(OUTER_TEXT, "Initialized")
            }
            return model.stopChange()
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
        override fun dispatch(model: Model, action: IAction): ArrayList<String> {
            model.startChange()
            if (action is ClickAction) {
                model.changeKey(OUTER_TEXT, "Done")
            }
            return model.stopChange()
        }

    }

    class ClickAction : IAction {

    }

    class InitValueAction : IAction {

    }
}
