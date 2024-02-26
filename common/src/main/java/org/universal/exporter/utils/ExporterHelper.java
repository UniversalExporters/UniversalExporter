package org.universal.exporter.utils;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;
import org.universal.exporter.UniExporterExpectPlatform;
import org.universal.exporter.command.type.ModidType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType.armorType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.BlockType.blockType;
import static org.universal.exporter.utils.ItemAndBlockUtils.nbt;
import static org.universal.exporter.utils.ItemAndBlockUtils.setItemName;

public class ExporterHelper {
    private static final Path exporter = UniExporterExpectPlatform.getGameFolder().resolve("exporter");
    private final String modid;
    private final Path modidDir;
    private final Path itemAndBlockExporter;
    public final Map<String, ArrayList<BlockAndItemSerializable>> items = new HashMap<>();
    public final Map<String, ArrayList<BlockAndItemSerializable>> noShowItems = new HashMap<>();
    public final Map<String, ArrayList<BlockAndItemSerializable>> recipeItems = new HashMap<>();
    private final ArrayList<ItemGroup> groups = new ArrayList<>();
    private final boolean advancement;
    private final BlockAndItems blockAndItems;
    private final CommandContext<ServerCommandSource> ctx;
    public ExporterHelper(ModidType modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        this(modid.asString(), advancement, ctx);
    }

    public ExporterHelper(String modid, boolean advancement, CommandContext<ServerCommandSource> ctx) {
        this.ctx = ctx;
        this.modid = modid;
        this.advancement = advancement;
        blockAndItems = new BlockAndItems();
        modidDir = exporter.resolve(modid);
        itemAndBlockExporter = modidDir.resolve("item-and-block.json");
        replaceDirPath(modidDir);
    }

    public void itemExporter() {
        itemGroupItemExporter();
        registryItemExporter();
        recipesItemExporter();
        saveItemExporter();
    }

    public void saveItemExporter() {
//        items.forEach(this::setItems);
//        noShowItems.forEach(this::setItems);//导出除tab之外的注册表内的物品
//        recipeItems.forEach(this::setItems);
        blockAndItems.save(itemAndBlockExporter);
    }

    private void setItems(String registerName, ArrayList<BlockAndItemSerializable> serializables) {
        for (BlockAndItemSerializable serializable : serializables) {
            if (serializable.type.asBlock != null) {
                if (serializable.type.asBlock.asFluid != null) {
                    blockAndItems.fluid(registerName, serializable);
                } else {
                    blockAndItems.block(registerName, serializable);
                }
            }
            else if (serializable.type.asFluid != null) {
                blockAndItems.bucket(registerName, serializable);
            }
            else if (serializable.type.armor != null) {
                blockAndItems.armor(registerName, serializable);
            }
            else if (serializable.type.tool != null) {
                blockAndItems.tool(registerName, serializable);
            }
            else if (serializable.type.asFood != null) {
                blockAndItems.food(registerName, serializable);
            }
            else {
                blockAndItems.item(registerName, serializable);
            }
        }
    }

    public void registryItemExporter() {
        for (Identifier id : Registries.ITEM.getIds()) {
            checkPut(id, id.asStack(), null);
//            if (!items.containsKey(id.toString()) && id.getNamespace().equals(modid)) {
//                put(id, new ItemStackHelper(Registries.ITEM.get(id).getDefaultStack(), null, advancement), recipeItems);
//            }
        }
    }
    //优先级 流体>方块>桶>盔甲>工具>刷怪蛋>食物>普通物品
    public void checkPut(Identifier id, ItemStack stack, ItemGroup group) {
        Item item = stack.getItem();
        BlockAndItemSerializable serializable = new BlockAndItemSerializable();
        setItemName(stack, serializable);
        initItem(stack, serializable, group);
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState defaultState = block.getDefaultState();
            serializable.type.asBlock = blockType(blockType -> {
                blockType.luminance = defaultState.getLuminance();
                blockType.isFull = Block.isShapeFullCube(defaultState.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));

                if (advancement) {
                    blockType.hasSidedTransparency = defaultState.hasSidedTransparency();
                    blockType.isAir = defaultState.isAir();
                    blockType.liquid = defaultState.isLiquid();
                    blockType.solid = defaultState.isSolid();
                    blockType.opaque = defaultState.isOpaque();
                    blockType.blockBreakParticles = defaultState.isOpaque();
                    blockType.replaceable = defaultState.isOpaque();
                    blockType.burnable = defaultState.isBurnable();
                    blockType.slipperiness = block.getSlipperiness();
                    blockType.velocityMultiplier = block.getVelocityMultiplier();
                    blockType.jumpVelocityMultiplier = block.getJumpVelocityMultiplier();
                    blockType.collidable = block.collidable;
                }

                blockType.hardness = defaultState.getHardness(null, null);
                blockType.toolRequired = defaultState.isToolRequired();
                blockType.resistance = block.getBlastResistance();

                blockType.randomTicks = defaultState.hasRandomTicks();
                blockType.lootTableId = block.getLootTableId().toString();
            });
            blockAndItems.block(id.toString(), serializable);
        } else if (item instanceof BucketItem bucketItem) {
            serializable.type.asFluid = Registries.FLUID.getId(bucketItem.fluid).toString();
            blockAndItems.bucket(id.toString(), serializable);
        } else if (item instanceof ArmorItem armorItem) {
            serializable.type.armor = armorType(armorType -> {
               armorType.type = armorItem.getType().getName();
               armorType.equipmentSlot = armorItem.getType().getEquipmentSlot().getName();
               armorType.enchantability = armorItem.getEnchantability();
               armorType.protection = armorItem.getProtection();
               armorType.toughness = armorItem.getToughness();
               armorType.knockbackResistance = armorItem.getMaterial().getKnockbackResistance();
                for (ItemStack matchingStack : armorItem.getMaterial().getRepairIngredient().getMatchingStacks()) {
                    String string = Registries.ITEM.getId(matchingStack.getItem()).toString();
                    NbtType nbt = new NbtType();
                    if (matchingStack.getNbt() != null) {
                        nbt(matchingStack.getNbt(), nbt);
                    }
                    armorType.repairIngredient(string, nbt);

                }
            });
            blockAndItems.armor(id.toString(), serializable);
        } else if (item instanceof ToolItem) {
            blockAndItems.tool(id.toString(), serializable);
        } else if (item instanceof SpawnEggItem) {
            blockAndItems.spawnEgg(id.toString(), serializable);
        } else if (item.isFood()) {
            blockAndItems.food(id.toString(), serializable);
        } else {
            blockAndItems.item(id.toString(), serializable);
        }
    }

    public void recipesItemExporter() {
        for (Recipe<?> recipe : ctx.getSource().getServer().getRecipeManager().values()) {
            ItemStack output = recipe.getOutput(ctx.getSource().getRegistryManager());
            Identifier id = Registries.ITEM.getId(output.getItem());
            checkPut(id, output, null);
//            put(id, new ItemStackHelper(output, null, advancement), recipeItems);
        }
    }

    public void put(Identifier id, ItemStackHelper helper, Map<String, ArrayList<BlockAndItemSerializable>> items) {
        if (equalsItem(id, helper)) {
            ArrayList<BlockAndItemSerializable> list = items.containsKey(id.toString()) ? items.get(id.toString()) : new ArrayList<>();
            list.add(helper);
            items.put(id.toString(), list);
        }
    }

    public boolean equalsItem(Identifier id, BlockAndItemSerializable serializable) {
        return
                (!(items.containsKey(id.toString()) || items.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid)) ||
                (!(noShowItems.containsKey(id.toString()) || noShowItems.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid)) ||
                (!(recipeItems.containsKey(id.toString()) || recipeItems.get(id.toString()).contains(serializable)) && id.getNamespace().equals(modid))
                ;
    }

    public void itemGroupItemExporter() {
        MinecraftClient instance = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
        if (networkHandler == null || instance.world == null) return;
        DynamicRegistryManager registryManager = instance.world.getRegistryManager();
        ItemGroups.updateDisplayContext(networkHandler.getEnabledFeatures(), true, registryManager);
        ArrayList<ItemGroup> groups = getGroups();
        for (ItemGroup group : ItemGroups.getGroups()) {
            if (!groups.contains(group)) {
                Collection<ItemStack> displayStacks = group.getDisplayStacks();
                for (ItemStack displayStack : displayStacks) {
                    Identifier id = Registries.ITEM.getId(displayStack.getItem());
                    checkPut(id, displayStack, group);
//                    put(id, new ItemStackHelper(displayStack, group, advancement), items);
                }
            }
        }

    }

    public void initItem(ItemStack stack, BlockAndItemSerializable serializable, ItemGroup group) {
        ItemAndBlockUtils.defaultItemProperties(stack, serializable, group, ctx, advancement);
//        ItemAndBlockUtils.tabPut(group, serializable);

    }

    public static void replaceDirPath(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            try {
                Files.delete(path);
            } catch (IOException ignored) {}
            replaceDirPath(path);
        }
    }

    public ArrayList<ItemGroup> getGroups() {
        if (groups.isEmpty())
            groups.addAll(ImmutableList.of(ItemGroups.HOTBAR, ItemGroups.INVENTORY, ItemGroups.SEARCH).stream().map(Registries.ITEM_GROUP::get).toList());
        return groups;
    }
}
