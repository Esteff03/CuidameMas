package com.synunezcamacho.cuidame;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    private List<ChatMessage> messages;
    private String currentUsername;

    public ChatAdapter(List<ChatMessage> messages, String currentUsername) {
        this.messages = messages;
        this.currentUsername = currentUsername;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).username.equals(currentUsername) ? TYPE_ME : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MeViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new OtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof MeViewHolder) {
            ((MeViewHolder) holder).message.setText(msg.content);
            ((MeViewHolder) holder).timestamp.setText(msg.insertedAt);
        } else if (holder instanceof OtherViewHolder) {
            ((OtherViewHolder) holder).username.setText(msg.username);
            ((OtherViewHolder) holder).message.setText(msg.content);
            ((OtherViewHolder) holder).timestamp.setText(msg.insertedAt);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MeViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        MeViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp_text);
        }
    }

    static class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, timestamp;

        OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_text);
            message = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp_text);
        }
    }
}
