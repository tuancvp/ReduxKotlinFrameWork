package com.aicardio.reduxfw.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.aicardio.reduxfw.R
import com.aicardio.reduxfw.iface.IAction
import com.aicardio.reduxfw.iface.IReducer
import com.aicardio.reduxfw.iface.ISubscriber
import com.aicardio.reduxfw.model.Model
import kotlinx.android.synthetic.main.folder_list_item.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.logging.Logger

class ListItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), FolderItemClickListener {

    override fun onItemClick(position: Int, isLongClick: Boolean) {
        val file_path =
            model.getValueByKey<JSONArray>(FILE_PATHS)?.getString(position) ?: "NO FILE"
        Logger.getLogger("ListItemView").warning("${file_path} ${isLongClick}")
        if (!isLongClick) {
            model.perform_action(InitAction(file_path))
        } else {
            itemChosenListener?.onItemChosen(file_path)
        }
    }

    fun getMyAdapter(): FolderListAdapter {
        return folderListAdapter
    }

    companion object {
        const val PATH_FOLDER = "path_folder"
        const val FILE_PATHS = "file_paths"
    }


    val model : Model
    val folderListAdapter : FolderListAdapter
    var itemChosenListener : ItemChosenListener? = null

    init {
        model = Model()
        folderListAdapter = FolderListAdapter(model, this)
        model.add_action(InitAction(), InitReducer())
        model.add_action(PopulateAction(), PopulateReducer())
        model.add_subscriber(PATH_FOLDER, PathFolderSubscriber())
        model.add_subscriber(FILE_PATHS, FilePathsSubscriber(this))
    }

    class FolderListAdapter(val model: Model, val listener: FolderItemClickListener) : RecyclerView.Adapter<FolderItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_list_item, parent, false)
            return FolderItemViewHolder(view, listener)
        }

        override fun getItemCount(): Int {
            return model.getValueByKey<JSONArray>(FILE_PATHS)?.length() ?: 0
        }

        override fun onBindViewHolder(holder: FolderItemViewHolder, position: Int) {
            holder.bind(model.getValueByKey<JSONArray>(FILE_PATHS)?.getString(position) ?: "NO FILE", position)
        }

    }

    class FolderItemViewHolder(val view: View, val listener: FolderItemClickListener ) : RecyclerView.ViewHolder(view), OnClickListener, OnLongClickListener {
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener.onItemClick(adapterPosition, false)
        }

        override fun onLongClick(p0: View?): Boolean {
            listener.onItemClick(adapterPosition, true)
            return true
        }

        fun bind(
            file_path: String,
            position: Int
        ) {
            view.tv_file_path.text = file_path
        }

    }


    class FilePathsSubscriber(val listItemView: ListItemView) : ISubscriber {
        override fun onDataChanged(data: JSONObject, changedKeys: ArrayList<String>) {
            if (changedKeys.contains(FILE_PATHS)) {
                Logger.getLogger("FilePathsSubscriber").warning(data.getJSONArray(FILE_PATHS).toString())
                listItemView.folderListAdapter.notifyDataSetChanged()
            }
        }

    }

    class InitAction(val path_folder: String = ""): IAction {

    }


    class InitReducer() : IReducer {
        override fun dispatch(model: Model, action: IAction): ArrayList<String> {
            model.startChange()
            if (action is InitAction) {
                val path_folder = action.path_folder
                model.changeKey(PATH_FOLDER, path_folder)
                model.perform_async_action(PopulateAction())
            }
            return model.stopChange()
        }


    }

    class PathFolderSubscriber() : ISubscriber {
        override fun onDataChanged(data: JSONObject, changedKeys: ArrayList<String>) {
            if (changedKeys.contains(PATH_FOLDER)) {
                Logger.getLogger("PathFolderSubscriber").warning(data.getString(PATH_FOLDER))
            }
        }

    }

    class PopulateAction() : IAction {

    }

    class PopulateReducer() : IReducer {
        override fun dispatch(model: Model, action: IAction): ArrayList<String> {
            model.startChange()
            if (action is PopulateAction) {
                val path_folder: String? = model.getValueByKey<String>(PATH_FOLDER)
                path_folder?.let {
                    val file = File(path_folder)

                    val o = JSONArray(file.listFiles()?.map { it.absolutePath }?: emptyList<String>())

                    model.changeKey(FILE_PATHS, o)
                }
            }

            return model.stopChange()
        }

    }

}

interface ItemChosenListener {
    fun onItemChosen(file_path: String)
}

interface FolderItemClickListener {
    fun onItemClick(position: Int, isLongClick: Boolean)

}
