package edu.northeastern.numad22fa_team51_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad22fa_team51_project.adapters.MemberListItemAdapter;
import edu.northeastern.numad22fa_team51_project.models.UserModel;

public abstract class MembersListDialog extends Dialog {

    private MemberListItemAdapter adapter = null;
    String tile;
    ArrayList<UserModel> users;


    public MembersListDialog(@NonNull Context context, ArrayList<UserModel> users, String title) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        this.tile = title;
        this.users = users;
        setContentView(view);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        setUpRecyclerView(view);
    }

    private void setUpRecyclerView(View view){
        TextView tvTitle = findViewById(R.id.tvTitle);
        RecyclerView rvList = findViewById(R.id.rvList);
        tvTitle.setText(tile);

        if (users.size() > 0){
            rvList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new MemberListItemAdapter(getContext(), users);
            rvList.setAdapter(adapter);

            adapter.setOnClickListener(new MemberListItemAdapter.onClickListener() {
                @Override
                public void onClick(int position, UserModel user, String action) {
                    dismiss();
                    onItemSelected(user, action);
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void onItemSelected(UserModel user, String action);
}
