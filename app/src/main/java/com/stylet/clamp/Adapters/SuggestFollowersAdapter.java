package com.stylet.clamp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stylet.clamp.Model.SuggessFollowers;
import com.stylet.clamp.Model.User;
import com.stylet.clamp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestFollowersAdapter extends RecyclerView.Adapter<SuggestFollowersAdapter.ViewHolder>{

    private List<User> suggest_list;
    public Context context;

    public SuggestFollowersAdapter(List<User> suggest_list, Context context) {
        this.suggest_list = suggest_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_item, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String userImage = suggest_list.get(position).getUserimage();
        String username = suggest_list.get(position).getUsername();

        holder.suggestUserName.setText(username);
        Glide.with(context)
                .load(userImage)
                .into(holder.suggestUserImage);

        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return suggest_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView suggestUserImage;
        private TextView suggestUserName;
        private Button follow_btn;

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            suggestUserImage = view.findViewById(R.id.suggest_user_image);
            suggestUserName = view.findViewById(R.id.suggest_user_name);
            follow_btn = view.findViewById(R.id.add_follower_btn);

        }
    }
}
