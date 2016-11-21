package com.srikara.contactspicker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.srikara.contactspicker.R;

/**
 * Created by vijayasrikaradas on 22/11/16.
 */
public class ContactsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private ClickListener mClickListener;

    TextView tvName;
    TextView tvPhone;
    CheckBox cbSelection;

    public ContactsHolder(View itemView, ClickListener clickListener) {
        super(itemView);

        mClickListener = clickListener;
        tvName = (TextView)itemView.findViewById(R.id.tv_contact_name);
        tvPhone = (TextView)itemView.findViewById(R.id.tv_contact_no);
        cbSelection = (CheckBox)itemView.findViewById(R.id.cb_selection);

        cbSelection.setClickable(false);
        cbSelection.setSelected(false);

        itemView.setOnClickListener(this);

        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mClickListener != null) {
            return mClickListener.onItemLongClicked(getAdapterPosition());
        }
        return false;
    }

    public interface ClickListener {
        void onItemClicked(int position);

        boolean onItemLongClicked(int position);
    }
}
