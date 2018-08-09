package dfutils.commands;

// -------------------------
// Created by: Timeraa
// Created at: 09.08.18
// -------------------------


import dfutils.utils.MessageUtils;
import dfutils.utils.downloadItem;
import dfutils.utils.listItems;
import dfutils.utils.uploadItem;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import java.net.URLEncoder;

public class CommandItem extends CommandBase implements IClientCommand {

    private static final Minecraft minecraft = Minecraft.getMinecraft();
    public static boolean isUploading = false;
    public static String itemName = "";
    public static String itemNameOld = "";
    public static boolean isDownloading = false;
    public static String itemID;
    public static String ownerUUID;
    public static int page;

    public String getName() {
        return "item";
    }

    public String getUsage(ICommandSender sender) {
        return "§c/item";
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] commandArgs) {
        if (minecraft.player.isCreative()) {
            if (commandArgs.length == 0) {
                minecraft.player.sendMessage(new TextComponentString("§e§m          §6 [ §eItem §6] §e§m          \n"));
                minecraft.player.sendMessage(new TextComponentString("§e❱§6❱ §e/item upload"));
                minecraft.player.sendMessage(new TextComponentString("§e❱§6❱ §e/item download"));
                minecraft.player.sendMessage(new TextComponentString("§e❱§6❱ §e/item list"));
                minecraft.player.sendMessage(new TextComponentString(""));
                minecraft.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1f, 0f);
            } else if (commandArgs[0].equalsIgnoreCase("upload")) {
                handleUploadItem(commandArgs);
            } else if (commandArgs[0].equalsIgnoreCase("download")) {
                handleDownloadItem(commandArgs);
            } else if (commandArgs[0].equalsIgnoreCase("list")) {
                handleListItems(commandArgs);
            }
        } else {
            MessageUtils.errorMessage("This command can only be used in Gamemode Creative!");
        }
    }

    private void handleUploadItem(String[] commandArgs) {
        if (commandArgs.length == 1) {
            minecraft.player.sendMessage(new TextComponentString("§e§m          §6 [ §eItem §6- §eUpload §6] §e§m          "));
            minecraft.player.sendMessage(new TextComponentString(""));
            minecraft.player.sendMessage(new TextComponentString("§e❱§6❱ §e/item upload §5<§dname§5>"));
            minecraft.player.sendMessage(new TextComponentString(""));
            minecraft.player.sendMessage(new TextComponentString("§d❱§5❱ §5<§dname§5>"));
            minecraft.player.sendMessage(new TextComponentString("    §d❱§5❱ §7Color codes allowed"));
            minecraft.player.sendMessage(new TextComponentString("    §d❱§5❱ §7Only 20 characters long"));
            minecraft.player.sendMessage(new TextComponentString("    §d❱§5❱ §7(excluding color codes)\n"));
            minecraft.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1f, 0f);
        } else if (minecraft.player.getHeldItemMainhand().isEmpty()) {
            minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cYou need to hold an item to upload it."));
            minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
        } else if (!isUploading) {
            itemName = "";
            for (int i = 0; i < commandArgs.length; i++) {
                if (i > 0) {
                    if (itemName.equals("")) {
                        itemName = commandArgs[i].replace("&", "§");
                    } else {
                        itemName = itemName + " " + commandArgs[i].replace("&", "§");
                    }
                }
            }

            String itemNameCheck = itemName;

            if (itemNameCheck.replace("§", "").length() <= 20) {
                System.out.println(itemNameOld);
                if (!itemNameOld.equals("") && itemName.equals(itemNameOld)) {
                    minecraft.player.sendMessage(new TextComponentString("§b❱§3❱ §bOverwriting \"§r" + itemName + "§b\"..."));
                    minecraft.player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                } else {
                    itemNameOld = "";
                    minecraft.player.sendMessage(new TextComponentString("§b❱§3❱ §bUploading \"§r" + itemName + "§b\"..."));
                    minecraft.player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                }
                isUploading = true;
                new Thread(new uploadItem()).start();
            } else {
                minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cName must not contain more than 20 characters."));
                minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
            }
        } else {
            minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cPlease wait until the current upload is finished."));
            minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
        }
    }

    private void handleDownloadItem(String[] commandArgs) {
        if (commandArgs.length == 1) {
            minecraft.player.sendMessage(new TextComponentString("§e§m          §6 [ §eItem §6- §eDownload §6] §e§m          "));
            minecraft.player.sendMessage(new TextComponentString(""));
            minecraft.player.sendMessage(new TextComponentString("§e❱§6❱ §e/item download §5<§did§5>"));
            minecraft.player.sendMessage(new TextComponentString(""));
            minecraft.player.sendMessage(new TextComponentString("§d❱§5❱ §5<§did§5>"));
            minecraft.player.sendMessage(new TextComponentString("    §d❱§5❱ §7ID which you get after"));
            minecraft.player.sendMessage(new TextComponentString("    §d❱§5❱ §7uploading an Item\n"));
            minecraft.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1f, 0f);
        } else {
            if (!isDownloading) {
                if (commandArgs.length >= 3) {
                    itemID = "";
                    ownerUUID = commandArgs[1];
                    for (int i = 0; i < commandArgs.length; i++) {
                        if (i > 1) {
                            if (itemID.equals("")) {
                                itemID = commandArgs[i];
                            } else {
                                itemID = itemID + " " + commandArgs[i];
                            }
                        }
                    }
                    System.out.println(URLEncoder.encode(itemID));
                } else {
                    ownerUUID = null;
                    itemID = commandArgs[1];
                }
                isDownloading = true;
                minecraft.player.sendMessage(new TextComponentString("§b❱§3❱ §bDownloading item..."));
                minecraft.player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                new Thread(new downloadItem()).start();
            } else {
                minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cPlease wait until the current download is finished."));
                minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
            }
        }
    }

    private void handleListItems(String[] commandArgs) {
        if (commandArgs.length >= 2) {
            try {
                page = Integer.parseInt(commandArgs[1]);
                if (page <= 0) {
                    page = 0;
                    minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cPlease enter a valid page."));
                    minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
                    return;
                }
            } catch (NumberFormatException e) {
                page = 0;
                minecraft.player.sendMessage(new TextComponentString("§c❱§4❱ §cPlease enter a valid page."));
                minecraft.player.playSound(SoundEvents.ENTITY_CAT_HURT, 1f, 1f);
                return;
            }
        } else page = 0;
        minecraft.player.sendMessage(new TextComponentString("§b❱§3❱ §bLoading items..."));
        minecraft.player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
        new Thread(new listItems()).start();
    }
}
