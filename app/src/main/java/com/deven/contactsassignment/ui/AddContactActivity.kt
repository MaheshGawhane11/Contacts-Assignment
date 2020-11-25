package com.deven.contactsassignment.ui

import android.content.ContentProviderOperation
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deven.contactsassignment.R


class AddContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var etName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        initViews()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                val name: String = etName.text.toString()
                val phoneNumber = etPhoneNumber.text.toString()
                if (validations(name, phoneNumber)) {
                    progressBar.visibility = View.VISIBLE
                    addContact(name, phoneNumber)
                }
            }
        }
    }

    private fun validations(name: String, phoneNumber: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                etName.error = resources.getString(R.string.errorName)
                false
            }
            TextUtils.isEmpty(phoneNumber) -> {
                etPhoneNumber.error = resources.getString(R.string.errorPhoneNumber)
                false
            }
            phoneNumber.length < 10 -> {
                etPhoneNumber.error = resources.getString(R.string.invalidPhoneNumber)
                false
            }
            else -> true
        }
    }

    private fun addContact(name: String, phoneNumber: String) {
        val ops = arrayListOf<ContentProviderOperation>()
        var op: ContentProviderOperation.Builder =
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
        ops.add(op.build())

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        ops.add(op.build())

        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
            .withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
        ops.add(op.build())

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            progressBar.visibility = View.GONE
            Toast.makeText(applicationContext, "Contact added", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ContactListActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Unable to save contact", Toast.LENGTH_SHORT).show()
            Log.e("Exception encountered:", e.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ContactListActivity::class.java)
        startActivity(intent)
        finish()
    }
}