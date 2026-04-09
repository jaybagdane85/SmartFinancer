package com.example.smartfinancer;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatbotDialog {

    private Dialog dialog;
    private Context context;
    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private ImageButton btnSendMessage;
    private ImageView btnCloseChat;

    private List<ChatMessage> messages;
    private ChatAdapter adapter;
    private AdvancedFinancialChatbot chatbot;

    public ChatbotDialog(Context context) {
        this.context = context;
        this.chatbot = new AdvancedFinancialChatbot(context);

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_chatbot_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        rvChatMessages = dialog.findViewById(R.id.rv_chat_messages);
        etChatMessage = dialog.findViewById(R.id.et_chat_message);
        btnSendMessage = dialog.findViewById(R.id.btn_send_message);
        btnCloseChat = dialog.findViewById(R.id.btn_close_chatbot);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(context));
        rvChatMessages.setAdapter(adapter);

        addBotMessage("Hello! I'm your financial assistant. I can help with financial questions, calculations, and even show your salary breakdown. How can I help you today?");

        btnSendMessage.setOnClickListener(v -> sendMessage());
        btnCloseChat.setOnClickListener(v -> dialog.dismiss());
    }

    public void show() {
        dialog.show();
    }

    private void sendMessage() {
        String message = etChatMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        addUserMessage(message);
        etChatMessage.setText("");

        new Handler().postDelayed(() -> {
            String response = chatbot.getResponse(message);

            if (response.equals("SALARY_BREAKDOWN_VIEW")) {
                addBotMessage("Here's your salary breakdown:");

                new Handler().postDelayed(() -> {
                    View salaryView = chatbot.getSalaryBreakdownView();
                    LinearLayout container = new LinearLayout(context);
                    container.addView(salaryView);

                    messages.add(new ChatMessage("SALARY_BREAKDOWN", ChatMessage.TYPE_BOT));
                    adapter.notifyItemInserted(messages.size() - 1);
                    rvChatMessages.smoothScrollToPosition(messages.size() - 1);
                }, 500);
            } else {
                addBotMessage(response);
            }
        }, 1000);
    }

    private void addUserMessage(String message) {
        messages.add(new ChatMessage(message, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChatMessages.smoothScrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String message) {
        messages.add(new ChatMessage(message, ChatMessage.TYPE_BOT));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChatMessages.smoothScrollToPosition(messages.size() - 1);
    }
}
