package com.appsease.status.saver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appsease.status.saver.R
import com.appsease.status.saver.interfaces.IClientCallback
import com.appsease.status.saver.model.WaClient

class ClientAdapter(
    private val context: Context,
    private val itemLayoutRes: Int,
    private val callback: IClientCallback
) :
    RecyclerView.Adapter<ClientAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var clients: List<WaClient> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(itemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = clients[position]
        holder.icon?.setImageDrawable(client.getIcon(context))
        holder.name?.text = client.displayName
        configureCheckIcon(holder, client)
    }

    private fun configureCheckIcon(holder: ViewHolder, client: WaClient) {
        val checkMode = callback.checkModeForClient(client)
        if (itemCount == 1) {
            holder.check?.isChecked = true
            holder.check?.isEnabled = false
            holder.itemView.isEnabled = false
        } else {
            holder.check?.isChecked = checkMode == IClientCallback.MODE_CHECKED
        }
    }

    override fun getItemCount(): Int = clients.size

    @SuppressLint("NotifyDataSetChanged")
    fun setClients(clients: List<WaClient>) {
        this.clients = clients
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var icon: ImageView? = itemView.findViewById(R.id.icon)
        var name: TextView? = itemView.findViewById(R.id.name)
        var check: CompoundButton? = itemView.findViewById(R.id.check)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val currentClient = clients[layoutPosition]
            callback.clientClick(currentClient)
        }
    }
}