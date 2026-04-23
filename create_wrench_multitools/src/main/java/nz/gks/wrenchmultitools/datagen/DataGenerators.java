package nz.gks.wrenchmultitools.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import nz.gks.wrenchmultitools.CreateWrenchMultitools;

import java.util.concurrent.CompletableFuture;

/**
 * Hooks into NeoForge's data generation event.
 *
 * Run from IDE or terminal: `./gradlew runClientData`
 * Generated files go to src/generated/resources/ which is included in the JAR.
 */
@EventBusSubscriber(modid = CreateWrenchMultitools.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Empty BlockTagsProvider — needed because ItemTagsProvider consumes block tags upstream.
        var blockTagsProvider = new net.neoforged.neoforge.common.data.BlockTagsProvider(
                packOutput, lookupProvider, CreateWrenchMultitools.MODID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider provider) { /* no block tags */ }
        };
        generator.addProvider(event.includeServer(), blockTagsProvider);

        // Client-side providers
        generator.addProvider(event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new ModLanguageProvider(packOutput));

        // Server-side providers
        generator.addProvider(event.includeServer(),
                new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(packOutput, lookupProvider,
                        blockTagsProvider.contentsGetter(), existingFileHelper));
    }
}
