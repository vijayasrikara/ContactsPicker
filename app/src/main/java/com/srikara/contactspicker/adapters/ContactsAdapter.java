package com.srikara.contactspicker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.srikara.contactspicker.R;
import com.srikara.contactspicker.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijayasrikaradas on 21/11/16.
 */

public class ContactsAdapter extends SelectableAdapter<ContactsHolder>{

    private ContactsHolder.ClickListener mClickListener;
    private Context mContext;
    private List<Contact> mDataList;

    public ContactsAdapter(Context context,
                           List<Contact> dataList,
                           ContactsHolder.ClickListener clickListener){
        mContext = context;
        mDataList = dataList;
        mClickListener = clickListener;
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.contact_list_item, parent, false);

        ContactsHolder holder = new ContactsHolder(view, mClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder, int position) {
        Contact contact = mDataList.get(position);
        holder.tvName.setText(contact.getName());
        if(contact.getNumberList() != null && !contact.getNumberList().isEmpty()){
            holder.tvPhone.setText(contact.getNumberList().get(0).getNumber());
        }
        holder.tvName.setText(contact.getName());

        holder.cbSelection.setChecked(isSelected(position));
        holder.itemView.setBackgroundColor(isSelected(position) ? Color.LTGRAY : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<Contact> getSelectedContacts() {
        List<Integer> selectedItems = getSelectedItems();

        List<Contact> contacts = new ArrayList<>(selectedItems.size());

        for (int i = 0; i < selectedItems.size(); ++i) {
            contacts.add(mDataList.get(selectedItems.get(i)));
        }

        return contacts;
    }

}
