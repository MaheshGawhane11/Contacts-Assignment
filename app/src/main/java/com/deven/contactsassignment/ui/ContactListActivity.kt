package com.deven.contactsassignment.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deven.contactsassignment.R
import com.deven.contactsassignment.adapter.ContactsRecyclerAdapter
import com.deven.contactsassignment.model.Contacts
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ContactListActivity : AppCompatActivity(), View.OnClickListener {
    private var contactList: ArrayList<Contacts> = ArrayList()
    private lateinit var contactsAdapter: ContactsRecyclerAdapter
    private lateinit var rvContacts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAddContact: FloatingActionButton
    private val requestReadWriteContacts = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        initViews()
        requestPermission()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check permissions for Read and Write contacts
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    ), requestReadWriteContacts
                )
            } else {
                progressBar.visibility = View.VISIBLE
                getContacts()
            }
        } else {
            progressBar.visibility = View.VISIBLE
            getContacts()
        }
    }

    private fun initViews() {
        rvContacts = findViewById(R.id.rvContacts)
        progressBar = findViewById(R.id.progressBar)
        fabAddContact = findViewById(R.id.fabAddContact)
        fabAddContact.setOnClickListener(this)

        contactsAdapter = ContactsRecyclerAdapter(this, contactList)
        rvContacts.layoutManager = LinearLayoutManager(this)
        rvContacts.adapter = contactsAdapter
        rvContacts.itemAnimator = DefaultItemAnimator()
        rvContacts.isNestedScrollingEnabled = true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddContact -> {
                val intent = Intent(this, AddContactActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getContacts() {
        contactList.clear()
        contactsAdapter.notifyDataSetChanged()
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(
            ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.CONTACT_ID
        )

        val uri: Uri = Phone.CONTENT_URI
        //Filter contact list
        val filter =
            "" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + Phone.TYPE + "=" + Phone.TYPE_MOBILE
        //Set contact list in ascending order
        val order = ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        val cursor = resolver.query(uri, projection, filter, null, order)
        var prevNumber = ""
        while (cursor!!.moveToNext()) {
            val contactNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER))
            //Below condition is to avoid duplicate contacts. It's fetching all account contacts like Phone Book, Whats App, Due etc.
            if (prevNumber != contactNumber) {
                val contacts = Contacts()
                contacts.name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                contacts.phone_number = contactNumber
                contactList.add(contacts)
            }
            prevNumber = contactNumber
        }
        progressBar.visibility = View.GONE
        contactsAdapter.notifyDataSetChanged()
        cursor.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == requestReadWriteContacts) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            } else {
                permissionAlert()
            }
        }
    }

    private fun permissionAlert() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Read & Write contact access needed")
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setMessage("Please enable access to contacts.")
        builder.setOnDismissListener {
            requestPermission()
        }
        builder.show()
    }
}