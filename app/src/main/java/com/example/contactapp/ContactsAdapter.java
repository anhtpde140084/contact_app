package com.example.contactapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements Filterable
        , StickyRecyclerHeadersAdapter<ContactsAdapter.HeaderViewHolder> {

    private ArrayList<Contact> contactList;
    private ArrayList<Contact> mContactListOld;
    private Context mContext;


    public ContactsAdapter(Context context, ArrayList<Contact> contactList) {
        this.mContext = context;
        this.contactList = contactList;
        this.mContactListOld = contactList;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getFullname());
        Bitmap bitmap = DataConvert.convertByteArrayToImage(contact.getAvt());
        if (bitmap != null) {
            holder.ivAvatar.setImageBitmap(bitmap);
            holder.tvAvatar.setVisibility(View.INVISIBLE);
        } else {
            Drawable drawable = holder.tvAvatar.getBackground();
            drawable.setColorFilter(contact.getColor(), PorterDuff.Mode.SRC_IN);
            holder.tvAvatar.setText(contact.getFirstName().substring(0,1));
            holder.tvAvatar.setBackground(drawable);
            holder.ivAvatar.setVisibility(View.INVISIBLE);
        }


        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(contactList.get(position));
            }
        });

    }

    private void onClickGoToDetail(Contact contact) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public long getHeaderId(int position) {
        return contactList.get(position).getFirstName().charAt(0);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int i) {
        String nameHeader = contactList.get(i).getFirstName().substring(0,1);
        headerViewHolder.tvHeaderItem.setText(nameHeader);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvHeaderItem;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderItem = (TextView) itemView.findViewById(R.id.tv_item_header);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAvatar;
        public ImageView ivAvatar;
        public CardView layoutItem;

        @SuppressLint("ResourceType")
        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvAvatar = (TextView) view.findViewById(R.id.tv_avatar);
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            layoutItem = (CardView) view.findViewById(R.id.layout_item);
        }
    }

    // search bar
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    contactList = mContactListOld;
                } else {
                    List<Contact> list = new ArrayList<>();
                    for (Contact contact : mContactListOld) {
                        if (contact.getFullname().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(contact);
                        }
                    }

                    contactList = (ArrayList<Contact>) list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactList = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
