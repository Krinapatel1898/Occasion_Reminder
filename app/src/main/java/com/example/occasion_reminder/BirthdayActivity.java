package com.example.occasion_reminder;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BirthdayActivity extends AppCompatActivity {

    String contactId,Name,Number,displayBirthday;
    ArrayList<String> nameArrayList;
    ArrayList<String> numberArrayList;
    AlertDialog.Builder builder;
    Cursor cursor;




    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_activity);

        TextView textView=findViewById(R.id.list_heading);
        textView.setText("Upcoming Birthdays");

        numberArrayList=new ArrayList<>();


        //Name ArrayList

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String current_Date = sdf.format(new Date());

        final ListView listView=findViewById(R.id.lv1);
        nameArrayList=new ArrayList<>();


        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, nameArrayList));

        cursor = getBirthdays();
        while (cursor.moveToNext()) {

            displayBirthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            contactId=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID));
            Name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Number=GetPhoneNumber(contactId);

            if (displayBirthday.equals(current_Date)) {
                nameArrayList.add(Name);
                numberArrayList.add(Number);
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    builder = new AlertDialog.Builder(BirthdayActivity.this);
                    builder.setMessage("Wish Now with :")
                            .setCancelable(true)
                            .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" +numberArrayList.get(position)));
                                    System.out.println("OnItemClickListener ==> listData ==> "+numberArrayList.get(position));
                                    startActivity(intent);


                                }
                            })
                            .setNegativeButton("Message", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    Uri uri1=Uri.parse("smsto: "+numberArrayList.get(position));
                                    Intent shareIntent =   new Intent(android.content.Intent.ACTION_SENDTO,uri1);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Happy Birthday to You");
                                    startActivity(Intent.createChooser(shareIntent, "Send Message via"));

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Occasion Reminder");
                    alert.show();

                }
            });
            System.out.println("\nonCreate() ==>ContactID =" + contactId + "\t Name = " + Name + "\tBirthday = " + displayBirthday + "Number = " + Number);

        }

    }



    private Cursor getBirthdays() {
        // Run query
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[] {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        return managedQuery(uri, projection, where, selectionArgs, null);
    }





    public String GetPhoneNumber(String id)
    {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Event.CONTACT_ID + " = " +  id,
                null, null);

        if(phones.getCount() > 0)
        {
            while(phones.moveToNext())
            {
                Number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }

        phones.close();

        return Number;
    }


}
