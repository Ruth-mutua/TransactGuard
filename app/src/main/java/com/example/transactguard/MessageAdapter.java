package com.example.transactguard;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<messageModel> messages;

    // Constructor and methods for updating data
    // Other methods in your adapter

    public void setMessages(List<messageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    // Inner ViewHolder class
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView creditCardNumberText;
        private TextView merchantIdPaidToText;
        private TextView amountPaidText;
        private TextView transactionStatusText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            creditCardNumberText = itemView.findViewById(R.id.creditCardNumberText);
            merchantIdPaidToText = itemView.findViewById(R.id.merchantIdPaidToText);
            amountPaidText = itemView.findViewById(R.id.amountPaidText);
            transactionStatusText = itemView.findViewById(R.id.transactionStatusText);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a message item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // Bind data to views
        messageModel message = messages.get(position);
        holder.creditCardNumberText.setText("Credit Card: " + message.getCreditCardNumber());
        holder.merchantIdPaidToText.setText("Merchant ID: " + message.getMerchantIdPaidTo());
        holder.amountPaidText.setText("Amount Paid: " + message.getAmountPaid());
        holder.transactionStatusText.setText("Status: " + message.getTransactionStatus());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
