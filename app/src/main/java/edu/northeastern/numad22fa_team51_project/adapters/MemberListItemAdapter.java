package edu.northeastern.numad22fa_team51_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.northeastern.numad22fa_team51_project.Constants;
import edu.northeastern.numad22fa_team51_project.R;
import edu.northeastern.numad22fa_team51_project.models.UserModel;

public class MemberListItemAdapter extends RecyclerView.Adapter<MemberListItemAdapter.MyViewHolder>{

    private MemberListItemAdapter.onClickListener onClickListener;
    private Context context;
    private ArrayList<UserModel> list;

    public MemberListItemAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberListItemAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel user = list.get(position);
        // For image
        Glide.with(context).load(user.getUser_img()).centerCrop().placeholder(R.drawable.ic_user_place_holder)
                .into(holder.member_image);

        holder.name.setText(user.getUser_name());
        holder.email.setText(user.getUser_email());

        if (user.getSelected()){
            holder.iv_selected.setVisibility(View.VISIBLE);
        }else{
            holder.iv_selected.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null){
                    if(user.getSelected()){
                        onClickListener.onClick(holder.getAdapterPosition(), user, Constants.UNSELECT);
                    }else{
                        onClickListener.onClick(holder.getAdapterPosition(), user, Constants.SELECT);
                    }
                }
            }
        });

    }

    public void setOnClickListener(MemberListItemAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener{
        void onClick(int position, UserModel user, String action);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView member_image;
        public TextView name;
        public TextView email;
        public ImageView iv_selected;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            member_image = itemView.findViewById(R.id.iv_member_image);
            name = itemView.findViewById(R.id.tv_member_name);
            email = itemView.findViewById(R.id.tv_member_email);
            iv_selected = itemView.findViewById(R.id.iv_selected_member);
        }
    }

}

