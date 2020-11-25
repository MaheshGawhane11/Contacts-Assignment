package com.deven.contactsassignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deven.contactsassignment.R
import com.deven.contactsassignment.model.Contacts

class ContactsRecyclerAdapter(var context: Context, var contactList: ArrayList<Contacts>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.tvName.text = contactList[position].name
        holder.tvPhoneNumber.text = contactList[position].phone_number
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvName: TextView = itemView.findViewById(R.id.tvName)
        internal var tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
    }
}