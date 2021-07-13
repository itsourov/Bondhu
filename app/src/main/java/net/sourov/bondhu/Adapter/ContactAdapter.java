package net.sourov.bondhu.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sourov.bondhu.EditContactDetails;
import net.sourov.bondhu.FriendsProfile;
import net.sourov.bondhu.Model.Contacts;
import net.sourov.bondhu.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    Context context;
    List<Contacts> contactsList;

    public ContactAdapter(Context context, List<Contacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }


    @NonNull
    @NotNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent,false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactAdapter.ContactHolder holder, int position) {


        Contacts contacts = contactsList.get(position);
        holder.contactName.setText(contacts.getName());
        holder.addressOnContactItem.setText(contacts.getAddress());
        Glide.with(context).load(contacts.getImageUrl()).placeholder(R.drawable.loading).into(holder.contactImg);

        holder.editOnContactItem.setOnClickListener(v -> {
            Intent goTOFriendProfile = new Intent(context, EditContactDetails.class);
            goTOFriendProfile.putExtra("name", contacts.getName());
            goTOFriendProfile.putExtra("number", contacts.getNumber());
            goTOFriendProfile.putExtra("dateOfBirth", contacts.getDateOfBirth());
            goTOFriendProfile.putExtra("address", contacts.getAddress());
            goTOFriendProfile.putExtra("image_url", contacts.getImageUrl());
            goTOFriendProfile.putExtra("unique_id", contacts.getUniqueID());
            context.startActivity(goTOFriendProfile);
        });
        holder.callOnContactItem.setOnClickListener(v -> {

            String phone = contacts.getNumber();
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", phone, null));
            context.startActivity(phoneIntent);

        });
        holder.deleteOnContactItem.setOnClickListener(v -> {

            FirebaseAuth mAuth;
            DatabaseReference reference;
            mAuth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Friends").child(contacts.getUniqueID());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm deleting " + contacts.getName() + " ...");
            builder.setIcon(R.drawable.delete);
            builder.setMessage("Are you sure you want to delete " + contacts.getName() + " from your friend list?");
            builder.setPositiveButton("Yes", (dialog, which) -> reference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
            builder.setNegativeButton("No", (dialog, which) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });
        holder.myFriendViewItem.setOnClickListener(v -> {
            Intent goTOFriendProfile = new Intent(context, FriendsProfile.class);
            goTOFriendProfile.putExtra("name", contacts.getName());
            goTOFriendProfile.putExtra("number", contacts.getNumber());
            goTOFriendProfile.putExtra("dateOfBirth", contacts.getDateOfBirth());
            goTOFriendProfile.putExtra("address", contacts.getAddress());
            goTOFriendProfile.putExtra("image_url", contacts.getImageUrl());
            goTOFriendProfile.putExtra("fd_added_date", contacts.getFd_added_date());
            goTOFriendProfile.putExtra("unique_id", contacts.getUniqueID());
            context.startActivity(goTOFriendProfile);
        });

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    static class ContactHolder extends RecyclerView.ViewHolder{

        CircleImageView contactImg;
        TextView contactName,addressOnContactItem;
        ImageView callOnContactItem,deleteOnContactItem,editOnContactItem;
        ConstraintLayout myFriendViewItem;

        public ContactHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.contactNameOnContactItem);
            contactImg = itemView.findViewById(R.id.imageViewOnContactItem);
            myFriendViewItem = itemView.findViewById(R.id.myFriendViewItem);
            addressOnContactItem = itemView.findViewById(R.id.addressOnContactItem);

            callOnContactItem = itemView.findViewById(R.id.callOnContactItem);
            deleteOnContactItem = itemView.findViewById(R.id.deleteOnContactItem);
            editOnContactItem = itemView.findViewById(R.id.editOnContactItem);

        }
    }
}
