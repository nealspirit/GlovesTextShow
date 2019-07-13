package com.glovestextshow.android;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glovestextshow.android.utils.SpeechUtils;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Msg> mMsgList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;

        TextView timeMsg;
        TextView leftMsg;
        TextView rightMsg;

        ImageView leftHorn;
        ImageView rightHorn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeMsg = itemView.findViewById(R.id.time_msg);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);
            leftMsg = itemView.findViewById(R.id.left_msg);
            rightMsg = itemView.findViewById(R.id.right_msg);
            leftHorn = itemView.findViewById(R.id.horn_left);
            rightHorn = itemView.findViewById(R.id.horn_right);
        }
    }

    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.msg_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MsgAdapter.ViewHolder viewHolder, int position) {
        Msg msg = mMsgList.get(position);
        viewHolder.timeMsg.setText(msg.getDate());
        if (msg.getType() == Msg.TYPE_RECEIVED){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
            viewHolder.leftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Text = viewHolder.leftMsg.getText().toString();
                    SpeechUtils.speekText(Text);
                }
            });
        }else if (msg.getType() == Msg.TYPE_SENT){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
            viewHolder.rightLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Text = viewHolder.rightMsg.getText().toString();
                    SpeechUtils.speekText(Text);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}
