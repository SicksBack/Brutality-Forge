package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.BooleanSetting;

import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoMath extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String pendingSolution = null;
    private final NumberSetting delay = new NumberSetting("Delay", this, 1000, 0, 5000, 0); // in milliseconds
    private final BooleanSetting autoSubmit = new BooleanSetting("Auto Submit", this, true);

    public AutoMath() {
        super("AutoMath", "Automatically solves math problems from chat.", Category.PLAYER);
        this.addSettings(delay, autoSubmit);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        String prefix = "QUICK MATHS! Solve: ";

        if (message.startsWith(prefix)) {
            String problem = message.substring(prefix.length());
            String equation = problem.replace("x", "*");

            try {
                double result = solveEquation(equation);
                long roundedResult = Math.round(result);

                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "QUICK MATHS! " +
                        EnumChatFormatting.GRAY + "Result: " + EnumChatFormatting.YELLOW + roundedResult));

                this.pendingSolution = Long.toString(roundedResult);

                if (autoSubmit.isEnabled()) {
                    scheduler.schedule(() -> {
                        mc.thePlayer.sendChatMessage("/ac " + this.pendingSolution);
                        this.pendingSolution = null;
                    }, (long) delay.getValue(), TimeUnit.MILLISECONDS);
                }
            } catch (Exception ex) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error solving math problem: " + ex.getMessage()));
            }
        }
    }

    private double solveEquation(String equation) throws Exception {
        equation = equation.replaceAll("\\s+", "");
        return evaluate(equation);
    }

    private double evaluate(String equation) throws Exception {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < equation.length(); ++i) {
            char ch = equation.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < equation.length() && (Character.isDigit(equation.charAt(i)) || equation.charAt(i) == '.')) {
                    sb.append(equation.charAt(i++));
                }
                values.push(Double.parseDouble(sb.toString()));
                --i;
                continue;
            }

            if (ch == '(') {
                operators.push(ch);
                continue;
            }

            if (ch == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
                continue;
            }

            if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private double applyOperation(char operator, double b, double a) throws Exception {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0.0) {
                    throw new Exception("Cannot divide by zero");
                }
                return a / b;
            default:
                throw new Exception("Unsupported operator: " + operator);
        }
    }
}
