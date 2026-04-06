package me.kimovoid.legacycombat.command;

import com.google.common.collect.ImmutableList;
import me.kimovoid.legacycombat.LegacyCombat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LegacyCombatCommand extends BukkitCommand {

    public LegacyCombatCommand(@NotNull String name) {
        super(name);
        this.setPermission("legacycombat.config");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String cmd, @NotNull String @NotNull [] args) {
        if (!this.testPermission(sender))
            return false;

        if (args.length < 1 || !args[0].equalsIgnoreCase("reload") && args.length < 2) {
            MiniMessage mm = MiniMessage.miniMessage();
            Component message = mm.deserialize("<gradient:#ff4a3d:#ff8e52>ʟᴇɢᴀᴄʏ ᴄᴏᴍʙᴀᴛ ꜱᴇᴛᴛɪɴɢꜱ</gradient><reset>")
                    .append(mm.deserialize("<br><gray>General:"))
                    .append(this.getValueMessage(mm, "debug", LegacyCombat.CONFIG.debug))
                    .append(this.getValueMessage(mm, "attack-frequency", LegacyCombat.CONFIG.attackFrequency))
                    .append(this.getValueMessage(mm, "projectile-tick-time", LegacyCombat.CONFIG.projTickTime))
                    .append(this.getValueMessage(mm, "rod-velocity", LegacyCombat.CONFIG.rodVelocity))
                    .append(this.getValueMessage(mm, "inflate-hitboxes", LegacyCombat.CONFIG.inflateHitboxes))
                    .append(this.getValueMessage(mm, "inflate-hitboxes-item", LegacyCombat.CONFIG.inflateHitboxesItem))
                    .append(mm.deserialize("<br><gray>Knockback:"))
                    .append(this.getValueMessage(mm, "kb-experimental", LegacyCombat.CONFIG.kbExperimental))
                    .append(this.getValueMessage(mm, "kb-friction", LegacyCombat.CONFIG.kbFriction))
                    .append(this.getValueMessage(mm, "kb-horizontal", LegacyCombat.CONFIG.kbHorizontal))
                    .append(this.getValueMessage(mm, "kb-vertical", LegacyCombat.CONFIG.kbVertical))
                    .append(this.getValueMessage(mm, "kb-vertical-limit", LegacyCombat.CONFIG.kbVerticalLimit))
                    .append(this.getValueMessage(mm, "kb-extra-horizontal", LegacyCombat.CONFIG.kbExtraHorizontal))
                    .append(this.getValueMessage(mm, "kb-extra-vertical", LegacyCombat.CONFIG.kbExtraVertical))
                    .append(this.getValueMessage(mm, "kb-extra-sprint", LegacyCombat.CONFIG.kbExtraSprint))
                    .append(mm.deserialize("<br><gray>Projectiles:"))
                    .append(this.getValueMessage(mm, "proj-horizontal", LegacyCombat.CONFIG.projHorizontal))
                    .append(this.getValueMessage(mm, "proj-vertical", LegacyCombat.CONFIG.projVertical))
                    .append(mm.deserialize("<br><gray>Bow-boost:"))
                    .append(this.getValueMessage(mm, "bb-horizontal", LegacyCombat.CONFIG.bbHorizontal))
                    .append(this.getValueMessage(mm, "bb-vertical", LegacyCombat.CONFIG.bbVertical))
                    .append(mm.deserialize("<br><gray>Self-rod:"))
                    .append(this.getValueMessage(mm, "sr-horizontal", LegacyCombat.CONFIG.srHorizontal))
                    .append(this.getValueMessage(mm, "sr-vertical", LegacyCombat.CONFIG.srVertical));

            sender.sendMessage(message);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            LegacyCombat.CONFIG.load();
            sender.sendMessage("Reloaded legacy-combat.yml from file");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "debug" -> LegacyCombat.CONFIG.debug = Boolean.parseBoolean(args[1]);
            case "attack-frequency" -> LegacyCombat.CONFIG.attackFrequency = getValueInt(args[1]);
            case "projectile-tick-time" -> LegacyCombat.CONFIG.projTickTime = getValueInt(args[1]);
            case "rod-velocity" -> LegacyCombat.CONFIG.rodVelocity = getValueDouble(args[1]);
            case "inflate-hitboxes" -> LegacyCombat.CONFIG.inflateHitboxes = (float) getValueDouble(args[1]);
            case "inflate-hitboxes-item" -> LegacyCombat.CONFIG.inflateHitboxesItem = (float) getValueDouble(args[1]);
            case "kb-experimental" -> LegacyCombat.CONFIG.kbExperimental = Boolean.parseBoolean(args[1]);
            case "kb-friction" -> LegacyCombat.CONFIG.kbFriction = getValueDouble(args[1]);
            case "kb-horizontal" -> LegacyCombat.CONFIG.kbHorizontal = getValueDouble(args[1]);
            case "kb-vertical" -> LegacyCombat.CONFIG.kbVertical = getValueDouble(args[1]);
            case "kb-vertical-limit" -> LegacyCombat.CONFIG.kbVerticalLimit = getValueDouble(args[1]);
            case "kb-extra-horizontal" -> LegacyCombat.CONFIG.kbExtraHorizontal = getValueDouble(args[1]);
            case "kb-extra-vertical" -> LegacyCombat.CONFIG.kbExtraVertical = getValueDouble(args[1]);
            case "kb-extra-sprint" -> LegacyCombat.CONFIG.kbExtraSprint = getValueDouble(args[1]);
            case "proj-horizontal" -> LegacyCombat.CONFIG.projHorizontal = getValueDouble(args[1]);
            case "proj-vertical" -> LegacyCombat.CONFIG.projVertical = getValueDouble(args[1]);
            case "bb-horizontal" -> LegacyCombat.CONFIG.bbHorizontal = getValueDouble(args[1]);
            case "bb-vertical" -> LegacyCombat.CONFIG.bbVertical = getValueDouble(args[1]);
            case "sr-horizontal" -> LegacyCombat.CONFIG.srHorizontal = getValueDouble(args[1]);
            case "sr-vertical" -> LegacyCombat.CONFIG.srVertical = getValueDouble(args[1]);
            default -> throw new CommandException("Invalid type: " + args[0].toLowerCase());
        }

        LegacyCombat.CONFIG.save();
        broadcastCommandMessage(sender, String.format("Set value of \"%s\" to → %s", args[0].toLowerCase(), args[1]));
        return true;
    }

    private double getValueDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid value: " + s);
        }
    }

    private int getValueInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid value: " + s);
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String toComplete = args[0].toLowerCase();
            String[] values = {
                    "reload",
                    "debug",
                    "attack-frequency",
                    "projectile-tick-time",
                    "rod-velocity",
                    "inflate-hitboxes",
                    "inflate-hitboxes-item",
                    "kb-experimental",
                    "kb-friction",
                    "kb-horizontal",
                    "kb-vertical",
                    "kb-vertical-limit",
                    "kb-extra-horizontal",
                    "kb-extra-vertical",
                    "kb-extra-sprint",
                    "proj-horizontal",
                    "proj-vertical",
                    "bb-horizontal",
                    "bb-vertical",
                    "sr-horizontal",
                    "sr-vertical"
            };

            for (String s : values) {
                if (StringUtil.startsWithIgnoreCase(s, toComplete)) {
                    completions.add(s);
                }
            }

            return completions;
        } else {
            return ImmutableList.of();
        }
    }

    private Component getValueMessage(MiniMessage mm, String name, Object value) {
        return mm.deserialize(String.format("<br> <gray>• <reset>%s: <#ffc054>%s", name, value));
    }
}