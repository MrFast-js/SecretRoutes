package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.utils.*;

import java.io.File;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class SRMConfig extends Config {
    @Switch(
            name = "Render Routes",
            description = "Main toggle",
            subcategory = "General"
    )
    public static boolean modEnabled = true;

    @Switch(
            name = "Render secrets when room is completed",
            description = "Renders secrets even if the room is completed",
            subcategory = "General"
    )
    public static boolean renderComplete = false;


    @Switch(
            name = "All secrets",
            description = "Render all secrets",
            subcategory = "General"
    )
    public static boolean allSecrets = false;


    @Switch(
            name = "Render steps",
            description = "Renders the entire path to the secret instead of just the secret",
            subcategory = "General"
    )
    public static boolean allSteps = false;

    @Info(
            text = "Does not display all secrets in room, only route",
            subcategory = "General",
            size = 2,
            type = InfoType.WARNING
    )
    public static boolean ignored;


    @Dropdown(
            name = "Line Type",
            options = {"Particles", "Lines", "None"},
            subcategory = "General",
            size = OptionSize.DUAL
    )
    public static int lineType = 0;

    @Dropdown(
            name = "Particle Type",
            options = {"Explosion Normal", "Explosion Large", "Explosion Huge", "Fireworks Spark", "Bubble", "Water Splash", "Water Wake", "Suspended", "Suspended Depth", "Crit", "Magic Crit", "Smoke Normal", "Smoke Large", "Spell", "Instant Spell", "Mob Spell", "Mob Spell Ambient", "Witch Magic", "Drip Water", "Drip Lava", "Villager Angry", "Villager Happy", "Town Aura", "Note", "Portal", "Enchantment Table", "Flame", "Lava", "Footstep", "Cloud", "Redstone", "Snowball", "Snow Shovel", "Slime", "Heart", "Barrier", "Water Drop", "Item Take", "Mob Appearance"},
            subcategory = "General"
    )
    public static int particles = 26;

    @Slider(
            name = "Tick inverval",
            description = "The interval between when the game renders the particles. Higher values will reduce lag, but may cause the particles to be less smooth",
            min = 0, max = 20.1F,
            step = 1,
            subcategory = "General"
    )
    public static int tickInterval = 1;


    @Slider(
            name = "Line width (not for particles)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int width = 5;


    @Slider(
            name = "Line width (for ender pearls)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int pearlLineWidth = 5;

    @DualOption(
            name = "Type of routes",
            left = "No pearls", right = "Pearls",
            description = "Toggle the default between pearls and no pearls",
            subcategory = "General",
            size = OptionSize.DUAL
    )
    public static boolean pearls = true;

    @Text(
            name = "Routes file name",
            description = "The file name used when No pearls is selected",
            placeholder = "routes.json",
            subcategory = "General"

    )
    public static String routesFileName = "routes.json";
    @Text(
            name = "Pearl routes file name",
            description = "The file name used when Pearls is selected",
            placeholder = "pearlroutes.json",
            subcategory = "General"

    )
    public static String pearlRoutesFileName = "pearlroutes.json";

    @Button(
            name = "Update routes",
            text = "Update routes",
            description = "Downloads the routes.json from github",
            subcategory = "General",
            size = 2
    )
    Runnable runnable = () -> {
        new Thread(() -> {
            if (pearls) {
                Main.updatePearlRoutes();
            } else {
                Main.updateRoutes();
            }
        }).start();
    };

    @Button(
            name = "Import routes",
            text = "Import routes",
            description = "Select a routes.json file to import, this will be copied to .minecraft/config/SecretRoutes/routes.json",
            size = 2,
            subcategory = "General"
    )
    Runnable runnable9 = () -> {
        new Thread(() -> {
            try {
                File file = FileUtils.promptUserForFile();
                if (file != null) {
                    FileUtils.copyFileToDirectory(file, Main.ROUTES_PATH);
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();
    };

    @Switch(
            name = "Notify for new updates",
            description = "Automatically checks for updates on startup. WILL NOT AUTO UPDATE",
            subcategory = "Updates"
    )
    public static boolean autoCheckUpdates = true;


    @Switch(
            name = "Auto download new updates",
            description = "Automatically downloads updates when they are available",
            subcategory = "Updates"
    )
    public static boolean autoDownload = false;


    @Button(
            name = "Check for updates",
            text = "Check for updates",
            description = "Manually check for an update if you wish to make sure",
            subcategory = "Updates",
            size = 2
    )
    Runnable runnable14 = () -> {
        new Thread(() -> {
            sendChatMessage("Checking for updates, please wait a few seconds...");
            Main.updateManager.checkUpdate();
        }).start();
    };

    // Recording

    @Button(
            name = "Set Bat Waypoint",
            text = "Set Bat Waypoint",
            description = "Since bat waypoints aren't automatically detected, sadly we must manually set a bat waypoint",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable3 = () -> {
        new Thread(() -> {
            if (Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage(EnumChatFormatting.YELLOW + "Added bat secret waypoint.");
            } else {
                sendChatMessage(EnumChatFormatting.RED + "Route recording is not enabled. Press the start recording button to begin.");
            }
        }).start();
    };

    @Button(
            name = "Start recording",
            text = "Start recording",
            description = "Start recording a custom secret route",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable2 = () -> {
        new Thread(() -> {
            Main.routeRecording.startRecording();
        }).start();
    };

    @Button(
            name = "Set Exit Waypoint",
            text = "Set Exit Waypoint",
            description = "Set an exit waypoint to at the end of your route",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable16 = () -> {
        new Thread(() -> {
            if (Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 0, -1); // The -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.EXITROUTE, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.stopRecording(); // Exiting the route, it should be stopped
                Main.routeRecording.setRecordingMessage("Added route exit waypoint & stopped recording.");
                LogUtils.info("Added route exit waypoint & stopped recording.");
            } else {
                sendChatMessage(EnumChatFormatting.RED + "Route recording is not enabled. Press the start recording button to begin.");
            }
        }).start();
    };

    @Button(
            name = "Stop recording",
            text = "Stop recording",
            description = "Stop recording your secret route",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable4 = () -> {
        new Thread(() -> {
            Main.routeRecording.stopRecording();
        }).start();
    };

    @Button(
            name = "Export routes",
            text = "Export routes",
            description = "Export your current secret routes to your downloads folder as routes.json",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable5 = () -> {
        new Thread(() -> {
            Main.routeRecording.exportAllRoutes();
        }).start();
    };

    @Button(
            name = "Import routes",
            text = "Import routes",
            description = "Import routes to recording from a routes.json in your downloads folder",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable6 = () -> {
        new Thread(() -> {
            Main.routeRecording.importRoutes("routes.json");
        }).start();
    };

    @HUD(
            name = "Recording info",
            category = "HUD"
    )
    public static RecordingHUD recordingHUD = new RecordingHUD();

    @HUD(
            name = "Current room",
            category = "HUD"
    )
    public static CurrentRoomHUD currentRoomHUD = new CurrentRoomHUD();

    //Color profile saving and loading
    @Text(
            name = "Color Profile Name",
            description = "The name of the color profile to save or load",
            subcategory = "Profiles",
            category = "Rendering",
            placeholder = "default.json"
    )
    public static String colorProfileName = "default.json";

    @Info(
            text = "Will auto append the .json extension if not provided",
            subcategory = "Profiles",
            category = "Rendering",
            type = InfoType.INFO
    )
    public static boolean b;

    @Button(
            name = "Save Color Profile",
            text = "Save",
            description = "Write the current color profile, excluding waypoints, to a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable10 = () -> {
        new Thread(() -> {
            Main.writeColorConfig(colorProfileName);
        }).start();
    };

    @Button(
            name = "Load Color Profile",
            text = "Load",
            description = "Reads the color profile from a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable11 = () -> {
        new Thread(() -> {
            if (Main.loadColorConfig(colorProfileName.isEmpty() ? "default.json" : colorProfileName)) {
                sendChatMessage(EnumChatFormatting.DARK_GREEN + "Loaded " + EnumChatFormatting.GREEN + colorProfileName + EnumChatFormatting.DARK_GREEN + " as color profile");
            }
        }).start();
    };

    @Button(
            name = "List all Color Profiles",
            text = "List",
            description = "Lists all the color profiles in the color profile directory",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable12 = () -> {
        new Thread(() -> {
            sendChatMessage("Color Profiles:", EnumChatFormatting.DARK_AQUA);
            for (String name : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)) {
                sendChatMessage(" - " + name, EnumChatFormatting.AQUA);
            }
        }).start();
    };

    @Button(
            name = "Import Color Profile",
            text = "Import",
            description = "Select a color profile to import, this will be copied to .minecraft/config/SecretRoutes/colorprofiles",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable13 = () -> {
        new Thread(() -> {
            try {
                File file = FileUtils.promptUserForFile();
                if (file != null) {
                    FileUtils.copyFileToDirectory(file, Main.COLOR_PROFILE_PATH);
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();
    };

    // Rendering
    @Slider(
            name = "Alpha multiplier",
            description = "Default opacity multiplier. ONLY HAS AN EFFECT ON THE FULL BLOCK",
            min = 0f, max = 1f,
            category = "Rendering",
            subcategory = "Waypoint Colors"
    )
    public static float alphaMultiplier = 0.5f;


    @Color(
            name = "Line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor lineColor = new OneColor(255, 0, 0);

    @Color(
            name = "Pearl line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor pearlLineColor = new OneColor(0, 255, 255);

    @Color(
            name = "EtherWarp",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor etherWarp = new OneColor(128, 0, 128);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean etherwarpFullBlock = false;

    @Color(
            name = "Mine",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor mine = new OneColor(255, 255, 0);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean mineFullBlock = false;

    @Color(
            name = "Interacts",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor interacts = new OneColor(0, 0, 255);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean interactsFullBlock = false;

    @Color(
            name = "superbooms",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor superbooms = new OneColor(255, 0, 0);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean superboomsFullBlock = false;

    @Color(
            name = "enderpearls",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor enderpearls = new OneColor(0, 255, 255);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean enderPearlFullBlock = false;

    @Color(
            name = "Secrets - item",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsItem = new OneColor(0, 255, 255);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean secretsItemFullBlock = false;

    @Color(
            name = "Secrets - interact",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsInteract = new OneColor(0, 0, 255);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean secretsInteractFullBlock = false;

    @Color(
            name = "Secrets - bat",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsBat = new OneColor(0, 255, 0);

    @Switch(
            name = "Full block",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static boolean secretsBatFullBlock = false;

    @Button(
            name = "Reset to default colors",
            text = "Reset",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    Runnable runnable7 = () -> {
        alphaMultiplier = 0.5f;
        lineColor = new OneColor(255, 0, 0);
        pearlLineColor = new OneColor(0, 255, 255);
        etherWarp = new OneColor(128, 0, 128);
        etherwarpFullBlock = false;
        mine = new OneColor(255, 255, 0);
        mineFullBlock = false;
        interacts = new OneColor(0, 0, 255);
        interactsFullBlock = false;
        superbooms = new OneColor(255, 0, 0);
        superboomsFullBlock = false;
        enderpearls = new OneColor(0, 255, 255);
        enderPearlFullBlock = false;
        secretsItem = new OneColor(0, 255, 255);
        secretsItemFullBlock = false;
        secretsInteract = new OneColor(0, 0, 255);
        secretsInteractFullBlock = false;
        secretsBat = new OneColor(0, 255, 0);
        secretsBatFullBlock = false;
    };


    // Start waypoints
    @Switch(
            name = "Start text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean startTextToggle = true;

    @Dropdown(
            name = "Start waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int startWaypointColorIndex = 12;

    @Slider(
            name = "Start waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float startTextSize = 3;

    // Exit route waypoints
    @Switch(
            name = "Exit text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean exitTextToggle = true;

    @Dropdown(
            name = "Exit waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int exitWaypointColorIndex = 12;

    @Slider(
            name = "Exit waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float exitTextSize = 3;

    // Interact waypoints
    @Switch(
            name = "Interact text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean interactTextToggle = true;

    @Dropdown(
            name = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactWaypointColorIndex = 9;

    @Slider(
            name = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactTextSize = 3;

    // Item waypoints
    @Switch(
            name = "Item text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean itemTextToggle = true;

    @Dropdown(
            name = "Item waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int itemWaypointColorIndex = 11;

    @Slider(
            name = "Item waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float itemTextSize = 3;

    // Bat waypoints
    @Switch(
            name = "Bat text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean batTextToggle = true;

    @Dropdown(
            name = "Bat waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int batWaypointColorIndex = 10;

    @Slider(
            name = "Bat waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float batTextSize = 3;

    // Etherwarp waypoints
    @Switch(
            name = "Etherwarp text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean etherwarpsTextToggle = false;

    @Switch(
            name = "Etherwarp enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the etherwarp waypoints"
    )
    public static boolean etherwarpsEnumToggle = false;

    @Dropdown(
            name = "Etherwarp waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int etherwarpsWaypointColorIndex = 5;

    @Slider(
            name = "Etherwarp waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float etherwarpsTextSize = 3;

    // Mines waypoints
    @Switch(
            name = "Stonk text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean minesTextToggle = false;

    @Switch(
            name = "Stonk enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the mines waypoints"
    )
    public static boolean minesEnumToggle = false;

    @Dropdown(
            name = "Stonk waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int minesWaypointColorIndex = 14;

    @Slider(
            name = "Stonk waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float minesTextSize = 3;

    // Interacts waypoints
    @Switch(
            name = "Interact text toggle",
            subcategory = "Waypoint Text Rendering",
            size = OptionSize.DUAL,
            category = "Rendering"
    )
    public static boolean interactsTextToggle = false;

    @Switch(
            name = "Interact enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the interact waypoints"
    )
    public static boolean interactsEnumToggle = false;

    @Dropdown(
            name = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactsWaypointColorIndex = 9;

    @Slider(
            name = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactsTextSize = 3;

    // Superboom waypoints
    @Switch(
            name = "Superboom text toggle",
            subcategory = "Waypoint Text Rendering",
            size = OptionSize.DUAL,
            category = "Rendering"
    )
    public static boolean superboomsTextToggle = false;

    @Switch(
            name = "Superboom enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the superboom waypoints"
    )
    public static boolean superboomsEnumToggle = false;

    @Dropdown(
            name = "Superboom waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int superboomsWaypointColorIndex = 12;

    @Slider(
            name = "Superboom waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float superboomsTextSize = 3;

    // Enderpearl waypoints
    @Switch(
            name = "Ender Pearl text toggle",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean enderpearlTextToggle = true;

    @Switch(
            name = "Enderpearl enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the superboom waypoints"
    )
    public static boolean enderpearlEnumToggle = false;

    @Dropdown(
            name = "Ender Pearl waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int enderpearlWaypointColorIndex = 11;

    @Slider(
            name = "Ender Pearl waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float enderpearlTextSize = 3;

    // Reset to text defaults
    @Button(
            name = "Reset to text defaults",
            text = "Reset",
            description = "Resets all the text options to their default values",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    Runnable runnable8 = () -> {
        startTextToggle = true;
        startWaypointColorIndex = 12;
        startTextSize = 3;
        exitTextToggle = true;
        exitWaypointColorIndex = 12;
        exitTextSize = 3;
        interactTextToggle = true;
        interactTextSize = 3;
        interactWaypointColorIndex = 10;
        itemTextToggle = true;
        itemWaypointColorIndex = 11;
        itemTextSize = 3;
        batTextToggle = true;
        batWaypointColorIndex = 10;
        batTextSize = 3;
        etherwarpsTextToggle = false;
        etherwarpsEnumToggle = false;
        etherwarpsWaypointColorIndex = 5;
        etherwarpsTextSize = 3;
        minesTextToggle = false;
        minesEnumToggle = false;
        minesWaypointColorIndex = 14;
        minesTextSize = 3;
        interactsTextToggle = false;
        interactsEnumToggle = false;
        interactsWaypointColorIndex = 9;
        interactsTextSize = 3;
        superboomsTextToggle = false;
        superboomsEnumToggle = false;
        superboomsWaypointColorIndex = 12;
        superboomsTextSize = 3;
        enderpearlTextToggle = true;
        enderpearlEnumToggle = false;
        enderpearlWaypointColorIndex = 11;
        enderpearlTextSize = 3;
    };

    @Text(
            name = "Dev password",
            description = "The password to access the dev options",
            subcategory = "General",
            category = "Dev",
            size = 2
    )
    public static String devPassword = "";

    @Switch(
            name = "Verbose logging",
            description = "Adds more detailed logging, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev",
            size = OptionSize.DUAL
    )
    public static boolean verboseLogging = false;

    @Switch(
            name = "Better recording",
            description = "Adds more detailed logging for recording, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseRecording = true;
    //More verbose logging options will come in future releases
    @Switch(
            name = "Better updating",
            description = "adds more detailed logging for updating, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseUpdating = true;

    @Switch(
            name = "Better info",
            description = "adds more detailed logging for info, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseInfo = false;

    @Switch(
            name = "Better rendering",
            description = "adds more detailed logging rendering, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseRendering = false;

    @Switch(
            name = "ActionBar info",
            description = "Send the actionbar in chat for debugging purposes",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean actionbarInfo = false;

    @Switch(
            name = "Force outdated",
            description = "Forces the version to be outdated, useful for testing the auto updater",
            subcategory = "General",
            category = "Dev"
    )
    public static boolean forceUpdateDEBUG = false;

    @Info(
            text = "Do not turn this on unless you know exactly what you are doing",
            type = InfoType.ERROR,
            category = "Dev",
            subcategory = "General"
    )
    public static boolean c;

    @Dropdown(
            name = "Custom Pearl Orientation (Unused)",
            options = {"Default", "SW", "NW", "NE", "SE"},
            category = "Dev"
    )
    public static int customPearlOrientation = 0;

    @KeyBind(
            name = "Next Secret",
            description = "Cycles to the next secret",
            category = "Keybinds",
            subcategory = "Secrets",
            size = OptionSize.DUAL
    )
    public static OneKeyBind nextSecret = new OneKeyBind(UKeyboard.KEY_RBRACKET);

    @KeyBind(
            name = "Last Secret",
            description = "Cycles to the last secret",
            category = "Keybinds",
            subcategory = "Secrets",
            size = OptionSize.DUAL
    )
    public static OneKeyBind lastSecret = new OneKeyBind(UKeyboard.KEY_LBRACKET);

    @KeyBind(
            name = "Toggle Secret rendering",
            description = "Toggles the rendering of secrets",
            category = "Keybinds",
            subcategory = "Secrets",
            size = OptionSize.DUAL
    )
    public static OneKeyBind toggleSecrets = new OneKeyBind(UKeyboard.KEY_BACKSLASH);

    @Switch(
            name = "Custom Secret Sound",
            description = "Plays a custom sound when a secret is found",
            category = "General",
            subcategory = "Sound",
            size = OptionSize.DUAL
    )
    public static boolean customSecretSound = false;

    @Dropdown(
            name = "Custom Secret Sound",
            options = {"mob.blaze.hit", "fire.ignite", "random.orb", "random.break", "mob.guardian.land.hit", "note.pling", "zyra.meow"},
            category = "General",
            subcategory = "Sound"
    )
    public static int customSecretSoundIndex = 6;

    @Slider(
            name = "Custom Secret Sound Volume",
            min = 0,
            max = 1.0f,
            category = "General",
            subcategory = "Sound"
    )
    public static float customSecretSoundVolume = 1.0f;

    @Slider(
            name = "Custom Secret Sound Pitch",
            min = 0,
            max = 2.0f,
            category = "General",
            subcategory = "Sound"
    )
    public static float customSecretSoundPitch = 1.0f;

    @Button(
            name = "Play Custom Secret Sound",
            text = "Play",
            description = "Plays the custom secret sound",
            category = "General",
            subcategory = "Sound",
            size = 2
    )
    public static Runnable runnable15 = () -> {
        SecretSounds.secretChime(true);
    };


    public Boolean lambda(String dependentOption) {
        try {

            return (boolean) optionNames.get(dependentOption).get();
        } catch (IllegalAccessException ignored) {
            sendVerboseMessage("Error in lambda function");
            return true;
        }
    }

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();

        try {
            optionNames.get("lineType").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("width").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("routesFileName").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable9").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable14").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("particles").addHideCondition(() -> !isEqualTo(lineType, 0));
            optionNames.get("particles").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("tickInterval").addHideCondition(() -> !isEqualTo(lineType, 0));
            optionNames.get("tickInterval").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("pearlLineWidth").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("pearls").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("pearlRoutesFileName").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("allSecrets").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("renderComplete").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("allSteps").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("allSteps").addHideCondition(() -> !lambda("allSecrets"));
            optionNames.get("ignored").addHideCondition(() -> !lambda("modEnabled"));

            optionNames.get("autoDownload").addHideCondition(() -> !lambda("autoCheckUpdates"));

            optionNames.get("startWaypointColorIndex").addHideCondition(() -> !lambda("startTextToggle"));
            optionNames.get("startTextSize").addHideCondition(() -> !lambda("startTextToggle"));
            optionNames.get("exitWaypointColorIndex").addHideCondition(() -> !lambda("exitTextToggle"));
            optionNames.get("exitTextSize").addHideCondition(() -> !lambda("exitTextToggle"));
            optionNames.get("interactWaypointColorIndex").addHideCondition(() -> !lambda("interactTextToggle"));
            optionNames.get("interactTextSize").addHideCondition(() -> !lambda("interactTextToggle"));
            optionNames.get("itemWaypointColorIndex").addHideCondition(() -> !lambda("itemTextToggle"));
            optionNames.get("itemTextSize").addHideCondition(() -> !lambda("itemTextToggle"));
            optionNames.get("batWaypointColorIndex").addHideCondition(() -> !lambda("batTextToggle"));
            optionNames.get("batTextSize").addHideCondition(() -> !lambda("batTextToggle"));

            optionNames.get("etherwarpsEnumToggle").addHideCondition(() -> !lambda("etherwarpsTextToggle"));
            optionNames.get("etherwarpsWaypointColorIndex").addHideCondition(() -> !lambda("etherwarpsTextToggle"));
            optionNames.get("etherwarpsTextSize").addHideCondition(() -> !lambda("etherwarpsTextToggle"));
            optionNames.get("minesEnumToggle").addHideCondition(() -> !lambda("minesTextToggle"));
            optionNames.get("minesWaypointColorIndex").addHideCondition(() -> !lambda("minesTextToggle"));
            optionNames.get("minesTextSize").addHideCondition(() -> !lambda("minesTextToggle"));
            optionNames.get("interactsEnumToggle").addHideCondition(() -> !lambda("interactsTextToggle"));
            optionNames.get("interactsWaypointColorIndex").addHideCondition(() -> !lambda("interactsTextToggle"));
            optionNames.get("interactsTextSize").addHideCondition(() -> !lambda("interactsTextToggle"));
            optionNames.get("superboomsEnumToggle").addHideCondition(() -> !lambda("superboomsTextToggle"));
            optionNames.get("superboomsWaypointColorIndex").addHideCondition(() -> !lambda("superboomsTextToggle"));
            optionNames.get("superboomsTextSize").addHideCondition(() -> !lambda("superboomsTextToggle"));
            optionNames.get("enderpearlEnumToggle").addHideCondition(() -> !lambda("enderpearlTextToggle"));
            optionNames.get("enderpearlWaypointColorIndex").addHideCondition(() -> !lambda("enderpearlTextToggle"));
            optionNames.get("enderpearlTextSize").addHideCondition(() -> !lambda("enderpearlTextToggle"));

            optionNames.get("forceUpdateDEBUG").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("verboseLogging").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("c").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("verboseRecording").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("verboseUpdating").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("verboseInfo").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("verboseRendering").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("actionbarInfo").addHideCondition(() -> !lambda("verboseLogging"));


            optionNames.get("customSecretSoundIndex").addHideCondition(() -> !lambda("customSecretSound"));
            optionNames.get("customSecretSoundVolume").addHideCondition(() -> !lambda("customSecretSound"));
            optionNames.get("customSecretSoundPitch").addHideCondition(() -> !lambda("customSecretSound"));
            optionNames.get("runnable15").addHideCondition(() -> !lambda("customSecretSound"));

            registerKeyBind(lastSecret, () -> {
                if (Utils.inCatacombs) {
                    Main.currentRoom.lastSecretKeybind();
                } else {
                    sendChatMessage("§cYou are not in a dungeon!");
                }
            });
            registerKeyBind(nextSecret, () -> {
                if (Utils.inCatacombs) {
                    Main.currentRoom.nextSecretKeybind();
                } else {
                    sendChatMessage("§cYou are not in a dungeon!");
                }
            });
            registerKeyBind(toggleSecrets, () -> {
                if (Utils.inCatacombs) {
                    Main.toggleSecretsKeybind();
                } else {
                    sendChatMessage("§cYou are not in a dungeon!");
                }
            });


        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public boolean isDevPasswordNotCorrect() {
        if (devPassword.equals("KyleIsMyDaddy")) {
            return false;
        }
        verboseLogging = false;
        forceUpdateDEBUG = false;
        return true;
    }

    public boolean isEqualTo(Object a, Object b) {
        return a.equals(b);
    }
}