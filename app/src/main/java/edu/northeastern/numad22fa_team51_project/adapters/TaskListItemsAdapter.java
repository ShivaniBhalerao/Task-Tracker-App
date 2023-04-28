package edu.northeastern.numad22fa_team51_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import edu.northeastern.numad22fa_team51_project.Constants;
import edu.northeastern.numad22fa_team51_project.R;
import edu.northeastern.numad22fa_team51_project.UpdateCardDetailsActivity;
import edu.northeastern.numad22fa_team51_project.models.BoardSerializable;
import edu.northeastern.numad22fa_team51_project.models.SelectedMembers;
import edu.northeastern.numad22fa_team51_project.models.TaskSerializableModel;
import edu.northeastern.numad22fa_team51_project.models.UserModel;

public class TaskListItemsAdapter extends RecyclerView.Adapter<TaskListItemsAdapter.TaskCardViewHolder> {

    private TaskListItemsAdapter.onClickListener onClickListener;
    private Context context;
    private ArrayList<TaskSerializableModel> arrCards;
    private BoardSerializable board;
    private ArrayList<UserModel> userObjects;

    public TaskListItemsAdapter(Context context, ArrayList<TaskSerializableModel> arrCards, BoardSerializable board, ArrayList<UserModel> userObjects) {
        this.context = context;
        this.arrCards = arrCards;
        this.board = board;
        this.userObjects = userObjects;
    }

    @NonNull
    @Override
    public TaskCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        TaskCardViewHolder vh = new TaskCardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskCardViewHolder holder, int position) {
        TaskSerializableModel task = arrCards.get(position);
        HashSet hSet = new HashSet();
        ArrayList<SelectedMembers> selectedMembers = new ArrayList<>();
        String owned_by = null;

        for(String s: task.getAssignedTo()){
            hSet.add(s);
        }

        for(UserModel user: userObjects){
            if (hSet.contains(user.getUser_id())){
                selectedMembers.add(new SelectedMembers(user.getUser_id(), user.getUser_img()));
            }
            if (user.getUser_id().equals(task.getCreatedBy())){
                owned_by = user.getUser_name();
            }
        }

        if (owned_by != null){
            holder.card_created_by.setText(new StringBuilder().append("Created By: ").append(owned_by));
            if (task.getIsComplete().equals(Constants.TRUE)){
                holder.card_created_by.setPaintFlags(holder.card_created_by.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.card_name.setText(task.getCard_name());
        if (task.getIsComplete().equals(Constants.TRUE)){
            holder.card_name.setPaintFlags(holder.card_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (task.getDueDate() != null && !task.getDueDate().equals("")){
            holder.due_date.setVisibility(View.VISIBLE);
            holder.due_date.setText(new StringBuilder().append("Due: ").append(task.getDueDate()).toString());
            if (task.getIsComplete().equals(Constants.TRUE)){
                holder.due_date.setPaintFlags(holder.due_date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        if(task.getIsComplete().equals(Constants.TRUE)){
            holder.img_task_done.setVisibility(View.VISIBLE);
        }

        if (selectedMembers.size() > 0) {

            holder.rv_selected_members_card.setVisibility(View.VISIBLE);
            holder.rv_selected_members_card.setLayoutManager(new GridLayoutManager(context, 6 ));

            SelectedMembersListAdapter adapter = new SelectedMembersListAdapter(context, selectedMembers, false);
            holder.rv_selected_members_card.setAdapter(adapter);
        }
        else{
            holder.rv_selected_members_card.setVisibility(View.GONE);
        }

        holder.points.setVisibility(View.VISIBLE);
        holder.points.setText(new StringBuilder().append("Points: ").append(task.getPoints()).toString());

        if(task.getIsComplete().equals(Constants.TRUE)){
            holder.points.setPaintFlags(holder.points.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateCardDetailsActivity.class);
                intent.putExtra(Constants.TASK_DETAILS, task);
                intent.putExtra(Constants.BOARD_OBJ, board);
                intent.putExtra(Constants.USERS_OBJ_ARR, userObjects);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrCards.size();
    }

    public void setOnClickListener(TaskListItemsAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener{
        void onClick(int position, TaskSerializableModel model);
    }

    public static class TaskCardViewHolder extends RecyclerView.ViewHolder{

        TextView card_name;
        TextView due_date;
        TextView points;
        TextView card_created_by;
        ImageView img_task_done;
        LinearLayout card_row;
        RecyclerView rv_selected_members_card;

        public TaskCardViewHolder(@NonNull View itemView) {
            super(itemView);
            card_name = itemView.findViewById(R.id.text_view_card_name);
            due_date = itemView.findViewById(R.id.text_view_card_due_date);
            points = itemView.findViewById(R.id.text_view_card_points);
            card_created_by = itemView.findViewById(R.id.text_view_card_created_by);
            rv_selected_members_card = itemView.findViewById(R.id.rv_selected_members_card);
            card_row = itemView.findViewById(R.id.card_row);
            img_task_done = itemView.findViewById(R.id.iv_task_complete);
        }
    }
}
