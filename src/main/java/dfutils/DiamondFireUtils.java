package dfutils;

import dfutils.codetools.CodeData;
import dfutils.codetools.codecopying.CopyController;
import dfutils.codetools.codecopying.CopyEventHandler;
import dfutils.codetools.codeprinting.PrintEventHandler;
import dfutils.codetools.commands.CommandNumberRange;
import dfutils.codetools.commands.CommandTextItem;
import dfutils.codetools.commands.CommandVarItem;
import dfutils.codetools.commands.locations.CommandLocBase;
import dfutils.codetools.misctools.CodeQuickSelection;
import dfutils.codetools.misctools.LocationHighlighting;
import dfutils.codetools.misctools.LocationSetter;
import dfutils.codetools.misctools.PistonHighlighting;
import dfutils.codetools.commands.code.CommandCodeBase;
import dfutils.codetools.selection.SelectionEventHandler;
import dfutils.commands.CommandHelp;
import dfutils.commands.CommandTest;
import dfutils.commands.itemcontrol.*;
import dfutils.commands.itemcontrol.candestroy.CommandCanDestroyBase;
import dfutils.commands.itemcontrol.canplace.CommandCanPlaceBase;
import dfutils.commands.itemcontrol.flags.CommandHideFlags;
import dfutils.commands.itemcontrol.flags.CommandSetFlags;
import dfutils.commands.itemcontrol.flags.CommandShowFlags;
import dfutils.commands.shortcuts.ShortcutLastMsg;
import dfutils.commands.shortcuts.ShortcutSupportChat;
import dfutils.commands.itemcontrol.attributes.CommandAttributeBase;
import dfutils.commands.itemcontrol.lore.CommandLoreBase;
import dfutils.commands.itemcontrol.rename.CommandRename;
import dfutils.commands.itemcontrol.rename.CommandRenameAnvil;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)
public class DiamondFireUtils {
    
    @Mod.Instance
    public static DiamondFireUtils instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        registerCommands();
        registerEvents();
        initializeData();
    }

    private void registerCommands() {
        ClientCommandHandler commandHandler = ClientCommandHandler.instance;

        //Item control command initialization.
        commandHandler.registerCommand(new CommandGive());
        commandHandler.registerCommand(new CommandItemData());
        commandHandler.registerCommand(new CommandAttributeBase());
        commandHandler.registerCommand(new CommandLoreBase());
        commandHandler.registerCommand(new CommandCanDestroyBase());
        commandHandler.registerCommand(new CommandCanPlaceBase());
        commandHandler.registerCommand(new CommandRenameAnvil());
        commandHandler.registerCommand(new CommandRename());
        commandHandler.registerCommand(new CommandUnbreakable());
        commandHandler.registerCommand(new CommandBreakable());
        commandHandler.registerCommand(new CommandDurability());
        commandHandler.registerCommand(new CommandHideFlags());
        commandHandler.registerCommand(new CommandShowFlags());
        commandHandler.registerCommand(new CommandSetFlags());

        //Code tools command initialization.
        commandHandler.registerCommand(new CommandCodeBase());
        commandHandler.registerCommand(new CommandLocBase());
        commandHandler.registerCommand(new CommandNumberRange());
        commandHandler.registerCommand(new CommandTextItem());
        commandHandler.registerCommand(new CommandVarItem());


        //Misc commands.
        commandHandler.registerCommand(new CommandHelp());
        commandHandler.registerCommand(new CommandTest());
    }

    private void registerEvents() {

        //Shortcut initialization.
        MinecraftForge.EVENT_BUS.register(new ShortcutLastMsg());
        MinecraftForge.EVENT_BUS.register(new ShortcutSupportChat());

        MinecraftForge.EVENT_BUS.register(new CommandGive());

        //Code tool event initialization.
        MinecraftForge.EVENT_BUS.register(new SelectionEventHandler());
        MinecraftForge.EVENT_BUS.register(new CopyController());
        MinecraftForge.EVENT_BUS.register(new CopyEventHandler());
        MinecraftForge.EVENT_BUS.register(new PrintEventHandler());
        MinecraftForge.EVENT_BUS.register(new PistonHighlighting());
        MinecraftForge.EVENT_BUS.register(new LocationSetter());
        MinecraftForge.EVENT_BUS.register(new LocationHighlighting());
        MinecraftForge.EVENT_BUS.register(new CodeQuickSelection());

        //Input event initialization.
        MinecraftForge.EVENT_BUS.register(new InputHandler());
    }

    private void initializeData() {
        new CodeData();
        InputHandler.initializeKeys();
    }
}