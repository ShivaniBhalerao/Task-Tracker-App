package edu.northeastern.numad22fa_team51_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import edu.northeastern.numad22fa_team51_project.R;
import edu.northeastern.numad22fa_team51_project.models.SelectedMembers;

public class SelectedMembersListAdapter extends RecyclerView.Adapter<SelectedMembersListAdapter.MyCardView> {

    private SelectedMembersListAdapter.onClickListener onClickListener;
    private Context context;
    private ArrayList<SelectedMembers> list;
    private boolean assign;

    public SelectedMembersListAdapter(Context context, ArrayList<SelectedMembers> list, boolean assign) {
        this.context = context;
        this.list = list;
        this.assign = assign;
    }

    @NonNull
    @Override
    public MyCardView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCardView(LayoutInflater.from(context).inflate(R.layout.item_card_selected_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCardView holder, int position) {
        SelectedMembers model = list.get(position);
        if (position == list.size() - 1 && assign){
            holder.add_member_image.setVisibility(View.VISIBLE);
            holder.selected_member_image.setVisibility(View.GONE);
        }else {
            holder.add_member_image.setVisibility(View.GONE);
            holder.selected_member_image.setVisibility(View.VISIBLE);

            Glide.with(context).load(model.getImage()).centerCrop().placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.selected_member_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null){
                    onClickListener.onClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(SelectedMembersListAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener{
        void onClick();
    }

    public static class MyCardView extends RecyclerView.ViewHolder{
        ImageView add_member_image;
        ImageView selected_member_image;
        public MyCardView(@NonNull View itemView) {
            super(itemView);
            add_member_image = itemView.findViewById(R.id.iv_add_member);
            selected_member_image = itemView.findViewById(R.id.iv_selected_member_image);

        }
    }
}