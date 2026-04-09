package com.startechnology.start_core.integration.create.schematic;

import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.startechnology.start_core.integration.create.CreateCompatPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SchematicClientHandler {

    private static final Map<UUID, Consumer<CompoundTag>> HANDLERS = new HashMap<>();

    public static void loadSchematicFromServer(BlockPos start, BlockPos end, Consumer<CompoundTag> consumer) {
        var uuid = UUID.randomUUID();
        HANDLERS.put(uuid, consumer);
        CreateCompatPackets.getChannel().sendToServer(new CreateSchematicPacket(uuid, start, end));
    }

    public static void handleResult(UUID uuid, CompoundTag structure) {
        HANDLERS.computeIfPresent(uuid, (_id, consumer) -> {
            consumer.accept(structure);
            return null;
        });
    }

    public static SchematicExport.SchematicExportResult saveSchematicFile(Path dir, String fileName, boolean overwrite, BlockPos first, BlockPos second, CompoundTag data) {
        var bb = BoundingBox.fromCorners(first, second);
        var origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        var bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

        if (fileName.isEmpty())
            fileName = CreateLang.translateDirect("schematicAndQuill.fallbackName").getString();
        if (!overwrite)
            fileName = FilesHelper.findFirstValidFilename(fileName, dir, "nbt");
        if (!fileName.endsWith(".nbt"))
            fileName += ".nbt";

        var file = dir.resolve(fileName).toAbsolutePath();
        try {
            Files.createDirectories(dir);
            var overwritten = Files.deleteIfExists(file);
            try (var out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                NbtIo.writeCompressed(data, out);
            }
            return new SchematicExport.SchematicExportResult(file, dir, fileName, overwritten, origin, bounds);
        } catch (IOException e) {
            Create.LOGGER.error("An error occurred while saving schematic [" + fileName + "]", e);
            return null;
        }
    }

}
