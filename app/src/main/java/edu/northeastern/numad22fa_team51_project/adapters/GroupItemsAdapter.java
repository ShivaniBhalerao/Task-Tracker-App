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
import java.util.ArrayList;
import edu.northeastern.numad22fa_team51_project.R;
import edu.northeastern.numad22fa_team51_project.models.BoardSerializable;

public class GroupItemsAdapter extends RecyclerView.Adapter<GroupItemsAdapter.MyViewHolder>  {

    private onClickListener onClickListener;
    private Context context;
    private ArrayList<BoardSerializable> list;

    public GroupItemsAdapter(Context context, ArrayList<BoardSerializable> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardSerializable board = list.get(position);
        // For image
        Glide.with(context).load(board.getGroup_image()).centerCrop().placeholder(R.drawable.ic_board_place_holder)
                .into(holder.display);
        // For group_name
        holder.groupname.setText(board.getGroup_name());
        // For created_by field
        holder.createdBy.setText("Created by: " + board.getGroup_created_by_user_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null){
                    onClickListener.onClick(holder.getAdapterPosition(), board);
                }
            }
        });
    }

    public void setOnClickListener(onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener{
        void onClick(int position, BoardSerializable model);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView display;
        public TextView groupname;
        public TextView createdBy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            display = itemView.findViewById(R.id.iv_board_image);
            groupname = itemView.findViewById(R.id.tv_name);
            createdBy = itemView.findViewById(R.id.tv_created_by);

        }
    }
}
