package uwu.smsgamer.antihackedclientantibot;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class Utils {
    public static int randomInt(int min, int max) {
        return ((int) (Math.random() * (max - min))) + min;
    }

    public static String generateRandomString(CharSequence charSequence, int min, int max) {
        char[] allowedChars = charSequence.toString().toCharArray();
        int length = randomInt(min, max);
        char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = allowedChars[randomInt(0, allowedChars.length - 1)];
        }
        return new String(chars);
    }

    public static ComponentLegalStrings generate(int on) {
        List<String> legalStrings = new ArrayList<>();
        String click = Vars.clicks.get(on);
        String header = Vars.header;
        String button = Vars.button;
        String footer = Vars.footer;
        List<Character> clickable = Vars.clickable;
        TextComponent component = new TextComponent();
        StringBuilder owo = new StringBuilder();
        boolean coloured = false;
        char colour = 'r';
        for (char c : header.toCharArray()) {
            if (clickable.contains(c)) {
                if (!owo.toString().isEmpty()) component.addExtra(owo.toString());
                owo = new StringBuilder();
                component.addExtra(generate("\u00A7" + colour + c, click).component);
            } else {
                if (c == '\u00A7')
                    coloured = true;
                else if (coloured) {
                    coloured = false;
                    colour = c;
                }
                owo.append(c);
            }
        }
        component.addExtra(owo.append("\n").toString());
        owo = new StringBuilder();

        for (char c : button.toCharArray()) {
            if (clickable.contains(c)) {
                if (!owo.toString().isEmpty()) component.addExtra(owo.toString());
                owo = new StringBuilder();
                ComponentLegalStrings cls = generate("\u00A7" + colour + c, click);
                legalStrings.add(cls.legalStrings.get(0));
                component.addExtra(cls.component);
            } else {
                if (c == '\u00A7')
                    coloured = true;
                else if (coloured) {
                    coloured = false;
                    colour = c;
                }
                owo.append(c);
            }
        }
        component.addExtra(owo.append("\n").toString());
        owo = new StringBuilder();

        for (char c : footer.toCharArray()) {
            if (clickable.contains(c)) {
                if (!owo.toString().isEmpty()) component.addExtra(owo.toString());
                owo = new StringBuilder();
                component.addExtra(generate("\u00A7" + colour + c, click).component);
            } else {
                if (c == '\u00A7')
                    coloured = true;
                else if (coloured) {
                    coloured = false;
                    colour = c;
                }
                owo.append(c);
            }
        }
        if (!owo.toString().isEmpty()) component.addExtra(owo.toString());

        return new ComponentLegalStrings(component, legalStrings);
    }

    private static ComponentLegalStrings generate(String c, String click) {
        TextComponent tp = new TextComponent(String.valueOf(c));
        String legal = click.replace("%m%", generateRandomString(Vars.allowedChars, Vars.msgLthMin, Vars.msgLthMax));
        tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, legal));
        return new ComponentLegalStrings(tp, legal);
    }

    public static void executeCommands(Player player, List<String> commands) {
        Bukkit.getScheduler().runTask(AntiHackedClientAntiBot.instance, () -> executeCommands0(player, commands));
    }

    private static void executeCommands0(Player player, List<String> commands) {
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaceString(player, cmd));
        }
    }

    public static String replaceString(OfflinePlayer player, String string) {
        string = ChatColor.translateAlternateColorCodes('&', string.replaceAll("%player%", player.getName()));
        if (Vars.papi)
            return PlaceholderAPI.setPlaceholders(player, string);
        else
            return string;
    }

    public static class ComponentLegalStrings {
        public final List<String> legalStrings;

        public final BaseComponent component;

        public ComponentLegalStrings(BaseComponent component, List<String> legalStrings) {
            this.legalStrings = legalStrings;
            this.component = component;
        }

        public ComponentLegalStrings(BaseComponent component, String... legalStrings) {
            this.legalStrings = Arrays.asList(legalStrings);
            this.component = component;
        }

    }
}
