package com.example.smartfinancer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedFinancialChatbot {

    private Context context;
    private View salaryBreakdownView;

    private static final String[] GREETINGS = {
            "Hello! I'm your financial assistant. How can I help you today?",
            "Hi there! I'm here to help with your financial questions.",
            "Welcome! I'm your AI financial advisor. What would you like to know?"
    };

    private static final String[] FALLBACKS = {
            "I'm not sure I understand. Could you rephrase your question?",
            "I don't have information on that yet. Is there something else I can help with?",
            "I'm still learning about that topic. Can I help you with something else?"
    };

    private static final Map<String, String[]> FINANCE_RESPONSES = new HashMap<>();
    private static final Map<String, String[]> GENERAL_RESPONSES = new HashMap<>();

    static {
        // Finance-related keywords and responses
        FINANCE_RESPONSES.put("budget", new String[]{
                "Start by tracking your income and expenses, then allocate funds to categories like housing, food, etc.",
                "Try the 50/30/20 rule: 50% needs, 30% wants, 20% savings/debt repayment.",
                "Use budgeting apps or cash envelopes to stay on track."
        });

        FINANCE_RESPONSES.put("expense", new String[]{
                "Cut down on non-essential spending.",
                "Use our expense tracker to see where your money goes.",
                "Separate expenses into 'needs' and 'wants' to prioritize better."
        });

        FINANCE_RESPONSES.put("save", new String[]{
                "Set up automatic transfers to savings accounts.",
                "Aim for 3–6 months' worth of expenses in an emergency fund.",
                "High-yield savings accounts offer better interest."
        });

        FINANCE_RESPONSES.put("invest", new String[]{
                "Index funds or ETFs are good for beginners.",
                "Diversify across assets to manage risk.",
                "Investing long-term usually beats short-term trading."
        });

        FINANCE_RESPONSES.put("mutual fund", new String[]{
                "Mutual funds pool money to invest in diversified assets.",
                "Look for low expense ratios.",
                "Index funds often have lower fees than active funds."
        });

        FINANCE_RESPONSES.put("stock", new String[]{
                "Stocks represent company ownership, earning via appreciation/dividends.",
                "Diversify across sectors for safety.",
                "Match stock investment with your risk level and timeline."
        });

        FINANCE_RESPONSES.put("debt", new String[]{
                "Try the avalanche or snowball method to pay off debt.",
                "Debt consolidation may help reduce interest costs.",
                "Focus on high-interest debt first."
        });

        FINANCE_RESPONSES.put("loan", new String[]{
                "Compare interest rates and terms before borrowing.",
                "Credit unions often offer better personal loan rates.",
                "Consider full loan cost, not just monthly payments."
        });

        FINANCE_RESPONSES.put("tax", new String[]{
                "Track deductible expenses all year.",
                "Use tax-advantaged accounts like 401(k)s or IRAs.",
                "For complex cases, consult a tax professional."
        });

        FINANCE_RESPONSES.put("retire", new String[]{
                "Start early to benefit from compounding.",
                "Aim to save 15% of your income (include employer match).",
                "Use traditional and Roth accounts for tax flexibility."
        });

        FINANCE_RESPONSES.put("insurance", new String[]{
                "Essential insurance: health, auto, home/renters, life, and disability (if needed).",
                "Review coverage yearly.",
                "Higher deductibles mean lower premiums—but ensure you can cover the deductible."
        });

        FINANCE_RESPONSES.put("salary", new String[]{
                "Research industry salaries and know your value before negotiating.",
                "Look at the total compensation package (benefits, bonuses, retirement).",
                "You can ask me to 'show salary breakdown' and I’ll break it down for you!"
        });

        FINANCE_RESPONSES.put("credit score", new String[]{
                "It’s influenced by history, utilization, account age, and mix.",
                "Pay on time, reduce balances, and avoid too many new accounts.",
                "Check your credit report regularly for errors."
        });

        // General keyword-based responses (non-financial)
        GENERAL_RESPONSES.put("weather", new String[]{
                "I can't check the weather, but I can help you save for a rainy day!",
                "Weather's out of scope—but your finances are not!",
                "Let's focus on building your financial future."
        });

        GENERAL_RESPONSES.put("sport", new String[]{
                "No scores here, but I can help you budget for season tickets!",
                "Want to save up for your favorite team's merch?",
                "Not a sports expert—just a money expert!"
        });

        GENERAL_RESPONSES.put("movie", new String[]{
                "Movies are fun—don’t forget to budget for entertainment!",
                "I can’t suggest movies, but I can help with subscriptions.",
                "Budgeting for entertainment is just as important!"
        });

        GENERAL_RESPONSES.put("food", new String[]{
                "Groceries and dining are major expenses—track them!",
                "I can help optimize your food budget.",
                "No recipes here—but I can help with the grocery list!"
        });

        GENERAL_RESPONSES.put("travel", new String[]{
                "Let’s plan a travel budget for your next adventure!",
                "Save first—explore later!",
                "Smart travel starts with smart budgeting."
        });
    }

    public AdvancedFinancialChatbot(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        salaryBreakdownView = inflater.inflate(R.layout.layout_salary_breakdown, null);
    }

    public String getResponse(String userInput) {
        String input = userInput.toLowerCase();

        if (isGreeting(input)) return getRandomResponse(GREETINGS);

        if (input.contains("salary breakdown") || input.contains("break down my salary") ||
                input.contains("show salary breakdown") || input.contains("salary structure")) {
            return "SALARY_BREAKDOWN_VIEW";
        }

        for (Map.Entry<String, String[]> entry : FINANCE_RESPONSES.entrySet()) {
            if (input.contains(entry.getKey())) {
                return getRandomResponse(entry.getValue());
            }
        }

        for (Map.Entry<String, String[]> entry : GENERAL_RESPONSES.entrySet()) {
            if (input.contains(entry.getKey())) {
                return getRandomResponse(entry.getValue());
            }
        }

        if (containsCalculation(input)) {
            return calculateResult(input);
        }

        return getRandomResponse(FALLBACKS);
    }

    private boolean isGreeting(String input) {
        return input.contains("hello") || input.contains("hi") ||
                input.contains("hey") || input.contains("greetings") ||
                input.equals("help");
    }

    private boolean containsCalculation(String input) {
        return input.contains("+") || input.contains("-") ||
                input.contains("*") || input.contains("/") ||
                input.contains("calculate") || input.contains("compute") ||
                input.contains("what is") || input.contains("how much is");
    }

    private String calculateResult(String input) {
        try {
            input = input.replaceAll("calculate", "")
                    .replaceAll("compute", "")
                    .replaceAll("what is", "")
                    .replaceAll("how much is", "")
                    .trim();

            Pattern pattern = Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                int num1 = Integer.parseInt(matcher.group(1));
                String operator = matcher.group(2);
                int num2 = Integer.parseInt(matcher.group(3));

                double result;
                switch (operator) {
                    case "+": result = num1 + num2; break;
                    case "-": result = num1 - num2; break;
                    case "*": result = num1 * num2; break;
                    case "/":
                        if (num2 == 0) return "I can't divide by zero!";
                        result = (double) num1 / num2; break;
                    default: return "Invalid operation.";
                }

                return "The result of " + num1 + " " + operator + " " + num2 + " is " + result;
            }
        } catch (Exception e) {
            return "I couldn't perform that calculation. Try something simpler like '5 + 3'.";
        }

        return "I couldn't understand the calculation. Try again like '5 + 3'.";
    }

    private String getRandomResponse(String[] responses) {
        return responses[new Random().nextInt(responses.length)];
    }

    public View getSalaryBreakdownView() {
        return salaryBreakdownView;
    }
}
