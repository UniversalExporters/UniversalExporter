package org.universal.exporter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancement.Advancement;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.uniexporter.exporter.adapter.serializable.Advancements;
import org.universal.exporter.command.argument.AdvancementParamArgumentType;
import org.universal.exporter.command.argument.ExporterArgumentType;
import org.universal.exporter.command.argument.ModidArgumentType;
import org.universal.exporter.command.type.AdvancementParamType;
import org.universal.exporter.command.type.ExporterType;
import org.universal.exporter.command.type.ModidType;
import org.universal.exporter.platform.Mod;
import org.universal.exporter.utils.CommandHelper;
import org.universal.exporter.utils.ExporterHelper;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.universal.exporter.command.argument.ExporterArgumentType.getExporter;
import static org.universal.exporter.command.argument.ModidArgumentType.getModidType;

/**
 * uex exporter command
 * @author baka4n
 * @author QWERTY770
 */
public class ExporterCommand extends CommandHelper implements Serializable {
    @Serial
    private static final long serialVersionUID = -3255258476049849876L;

    public static final ExecutorService executorService = Executors.newWorkStealingPool();//线程池子
    //命令头
    public static final LiteralArgumentBuilder<ServerCommandSource> uex = literal("uex");
    //带选择的命令
    public static final  RequiredArgumentBuilder<ServerCommandSource, ExporterType> select = argument("select", ExporterArgumentType.exporter());
    public static final RequiredArgumentBuilder<ServerCommandSource, Boolean> advanceParameters = argument("advance-parameters", BoolArgumentType.bool());
    public static final RequiredArgumentBuilder<ServerCommandSource, ModidType> modid = argument("modid", ModidArgumentType.modids());
    public static final RequiredArgumentBuilder<ServerCommandSource, AdvancementParamType>  advancementParam = argument("params", AdvancementParamArgumentType.advancementParams());

    private final ExporterType this$select;
    private final Boolean this$advanceParameters;
    private static final Path exporter = Mod.getGameFolder().resolve("exporter");
    private final AdvancementParamType[] types;

    private ExporterCommand(CommandContext<ServerCommandSource> context, ExporterType select, ModidType modid, boolean advanceParameters, int paramCount) {
        super(modid, context);
        this$select = select;
        this$advanceParameters = advanceParameters;
        types = new AdvancementParamType[paramCount];
        for (int i = 0; i < paramCount; i++) {
            types[i] = AdvancementParamArgumentType.getAdvancementParamType(context, "params_" + i);
        }
    }

    public static RequiredArgumentBuilder<ServerCommandSource, AdvancementParamType> registerParams(boolean select, boolean modid, boolean advanceParameters, int i) {

        if (i == AdvancementParamType.values().length - 1) {
            return argument("params_" + i, AdvancementParamArgumentType.advancementParams()).executes(ctx -> getInstance(ctx, select, modid, advanceParameters, i + 1));
        } else {
            return argument("params_" + i, AdvancementParamArgumentType.advancementParams()).then(registerParams(select, modid, advanceParameters, i + 1));
        }
    }

    /**
     * Register Server Command
     * @param dispatcher adapter
     * @param registryAccess access
     * @param env environment
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {




        dispatcher.register(
                uex
                        .executes(context ->getInstance(context, false, false, false, 0))
                        .then(
                                modid
                                        .executes(context -> getInstance(context, false, true, false, 0))
                                        .then(
                                                advanceParameters
                                                        .executes(ctx -> getInstance(ctx,false, true, true, 0))
                                                        .then(registerParams(false, true, true, 0))
                                        )
                        ).then(
                                advanceParameters
                                        .executes(ctx -> getInstance(ctx,false, false, true, 0))
                                        .then(registerParams(false, false, true, 0))
                        )
                        .then(
                                select
                                        .executes(context -> getInstance(context, true, false, false, 0))
                                        .then(
                                                advanceParameters
                                                        .executes(ctx -> getInstance(ctx,true, false, true, 0))
                                                        .then(
                                                                advanceParameters
                                                                        .executes(ctx -> getInstance(ctx,true, false, true, 0))
                                                                        .then(registerParams(true, false, true, 0))
                                                        )
                                        )
                                        .then(
                                                modid
                                                        .executes(context -> getInstance(context, true, true, false, 0))
                                                        .then(
                                                                advanceParameters
                                                                        .executes(ctx -> getInstance(ctx,true, true, true, 0))
                                                                        .then(registerParams(true, true, true, 0))
                                                        )
                                        )
                        )
        );

    }

    // uex √
    // uex advance-parameters √
    // uex select √
    // uex select modid √
    // uex modid √
    // uex modid advance-parameters √
    // uex select advance-parameters √
    // uex select modid advance-parameters √

    public static int getInstance(CommandContext<ServerCommandSource> context, boolean select, boolean modid, boolean advanceParameters, int paramCount) {
        return new ExporterCommand(context, select ? getExporter(context, "select") : null, modid ? getModidType(context, "modid") : null, advanceParameters && BoolArgumentType.getBool(context, "advance-parameters"), paramCount).all();
    }




    public static int defaultCommandSources() {
        return 1;
    }

    public static <T extends ArgumentBuilder<ServerCommandSource, T>, R extends ArgumentBuilder<ServerCommandSource, T>> T executes(R r, Command<ServerCommandSource> sourceCommand) {
        return r.executes(sourceCommand);
    }

    public int all() {
        if (this$select == null) {
            itemAndBlockExporterAll();
            advancementsAll();
        } else {
            if (this$select.equals(ExporterType.itemandblock)) {
                itemAndBlockExporterAll();
            } else if (this.this$select.equals(ExporterType.advancements)) {
                advancementsAll();
            }
        }

        return defaultCommandSources();
    }


    public void advancementModId(String modid) {
        Path advancementsJson = exporter.resolve(modid).resolve("advancements.json");
        List<Advancement> advancements = context.getSource().getServer().getAdvancementLoader().getAdvancements().stream().toList();

        Advancements modidAdvancements = new Advancements();
        advancements.stream().filter(advancement -> advancement.getParent() == null).forEachOrdered(parent -> {

        });

        subAdvancementSet(modidAdvancements, advancements);
        modidAdvancements.save(advancementsJson);
    }

    private void subAdvancementSet(Advancements modidAdvancements, List<Advancement> advancements) {
        modidAdvancements.advancements.forEach((registerName, serializable) -> {
            advancements.stream()
                    .filter(advancement -> advancement.getParent() != null && advancement.getParent().getId().toString().equals(registerName))
                    .forEachOrdered(subAdvancement -> {
                        serializable.children(advancements1 -> {

                        });
                        subAdvancementSet(serializable.children, advancements);
                    });
        });
    }

    /**
     * Exporting all advancement
     */
    public void advancementsAll() {
        if (this$modid != null) {
            advancementModId(this$modid.asString());
        } else {
            Arrays.stream(ModidType.values()).map(ModidType::name).forEach(this::advancementModId);
        }
    }
    //优先级 流体>方块>桶>盔甲>工具>食物>普通物品
    public void itemAndBlockExporterModid(String modid) {
        ExporterHelper exporterHelper = new ExporterHelper(modid, this$advanceParameters, context, types);
        exporterHelper.itemExporter();

    }

    /**
     * Exporting item and block
     */
    public void itemAndBlockExporterAll() {
        if (this$modid != null) {
            itemAndBlockExporterModid(this$modid.asString());
        } else {
            for (ModidType modidType : ModidType.values()) {
                itemAndBlockExporterModid(modidType.name());
            }
        }
    }


}
