package com.demokiller.host.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.demokiller.host.R
import com.demokiller.host.database.Contact

class ContactAdapter :
        PagedListAdapter<Contact, ContactViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact? = getItem(position)
        holder.nameView.text = contact?.name
    }

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Contact>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldContact: Contact,
                                         newContact: Contact) = oldContact.uid == newContact.uid

            override fun areContentsTheSame(oldContact: Contact,
                                            newContact: Contact) = oldContact == newContact
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(parent)
    }
}

class ContactViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)) {
    val nameView = itemView.findViewById<TextView>(R.id.name)
}