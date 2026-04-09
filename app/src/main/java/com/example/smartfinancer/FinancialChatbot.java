package com.example.smartfinancer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FinancialChatbot {

    private static final String[] GREETINGS = {
            "Hello! I'm your financial assistant. How can I help you today?",
            "Hi there! I'm here to help with your financial questions.",
            "Welcome! I'm your AI financial advisor. What would you like to know?"
    };

    private static final String[] FALLBACKS = {
            "I'm not sure I understand. Could you rephrase your question about finances?",
            "I don't have information on that yet. Is there something else about your finances I can help with?",
            "I'm still learning about that topic. Can I help you with budgeting, saving, or investments instead?"
    };

    private static final Map<String, String[]> RESPONSES = new HashMap<>();

    static {
        // Budget and Expense related responses
        RESPONSES.put("budget", new String[]{
                "Creating a budget is essential. Start by tracking your income and expenses, then allocate funds to different categories like housing, food, transportation, etc.",
                "A good budgeting rule is the 50/30/20 rule: 50% for needs, 30% for wants, and 20% for savings and debt repayment.",
                "To stick to your budget, try using cash envelopes or budgeting apps that track your spending in real-time."
        });

        RESPONSES.put("expense", new String[]{
                "To reduce expenses, identify non-essential spending and look for areas to cut back.",
                "Track your expenses using our app's expense tracker feature to see where your money is going.",
                "Consider categorizing expenses as 'needs' vs 'wants' to help prioritize your spending."
        });

        // Saving related responses
        RESPONSES.put("save", new String[]{
                "Start saving by setting up automatic transfers to a separate savings account on payday.",
                "For emergency funds, aim to save 3-6 months of living expenses.",
                "Consider high-yield savings accounts to earn more interest on your savings."
        });

        // Investment related responses
        RESPONSES.put("invest", new String[]{
                "For beginners, consider starting with index funds or ETFs which offer diversification.",
                "Remember to diversify your investments across different asset classes to manage risk.",
                "Long-term investing typically outperforms short-term trading. Consider your time horizon when making investment decisions."
        });

        RESPONSES.put("mutual fund", new String[]{
                "Mutual funds pool money from many investors to buy a diversified portfolio of stocks, bonds, or other securities.",
                "Look for mutual funds with low expense ratios to maximize your returns.",
                "Consider index funds, which typically have lower fees than actively managed mutual funds."
        });

        RESPONSES.put("stock", new String[]{
                "Stocks represent ownership in a company and can provide returns through price appreciation and dividends.",
                "Diversify your stock investments across different sectors and company sizes to reduce risk.",
                "Consider your risk tolerance and investment timeline before investing in individual stocks."
        });

        // Debt related responses
        RESPONSES.put("debt", new String[]{
                "To tackle debt, consider the avalanche method (paying highest interest first) or the snowball method (paying smallest balances first).",
                "Consolidating high-interest debt can sometimes help reduce interest payments.",
                "Prioritize paying off high-interest debt like credit cards before focusing on lower-interest debt like mortgages."
        });

        RESPONSES.put("loan", new String[]{
                "Before taking a loan, compare interest rates and terms from multiple lenders.",
                "For personal loans, credit unions often offer better rates than traditional banks.",
                "Consider the total cost of the loan, including interest and fees, not just the monthly payment."
        });

        // Tax related responses
        RESPONSES.put("tax", new String[]{
                "Keep track of tax-deductible expenses throughout the year to maximize your deductions.",
                "Consider tax-advantaged accounts like 401(k)s or IRAs for retirement savings.",
                "If your tax situation is complex, consulting with a tax professional can often save you money in the long run."
        });

        // Retirement related responses
        RESPONSES.put("retire", new String[]{
                "Start saving for retirement as early as possible to benefit from compound interest.",
                "Aim to save at least 15% of your income for retirement, including any employer match.",
                "Consider diversifying your retirement savings across different account types (traditional, Roth) for tax flexibility in retirement."
        });

        // Insurance related responses
        RESPONSES.put("insurance", new String[]{
                "Essential insurance includes health, auto, home/renters, and possibly life and disability depending on your situation.",
                "Review your insurance coverage annually to ensure it still meets your needs.",
                "Consider higher deductibles to lower premium costs, but make sure you have enough savings to cover the deductible if needed."
        });
    }

    public String getResponse(String userInput) {
        // Convert to lowercase for easier matching
        String input = userInput.toLowerCase();

        // Check if it's a greeting
        if (isGreeting(input)) {
            return getRandomResponse(GREETINGS);
        }

        // Look for keywords in the user input
        for (Map.Entry<String, String[]> entry : RESPONSES.entrySet()) {
            if (input.contains(entry.getKey())) {
                return getRandomResponse(entry.getValue());
            }
        }

        // If no match is found, return a fallback response
        return getRandomResponse(FALLBACKS);
    }

    private boolean isGreeting(String input) {
        return input.contains("hello") ||
                input.contains("hi") ||
                input.contains("hey") ||
                input.contains("greetings") ||
                input.equals("help");
    }

    private String getRandomResponse(String[] responses) {
        Random random = new Random();
        return responses[random.nextInt(responses.length)];
    }
}

