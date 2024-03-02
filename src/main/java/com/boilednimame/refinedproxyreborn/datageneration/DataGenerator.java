package com.boilednimame.refinedproxyreborn.datageneration;

import com.boilednimame.refinedproxyreborn.RP;
import com.refinedmods.refinedstorage.datageneration.BlockEntityTagGenerator;
import com.refinedmods.refinedstorage.datageneration.BlockModelGenerator;
import com.refinedmods.refinedstorage.datageneration.BlockTagGenerator;
import com.refinedmods.refinedstorage.datageneration.ItemTagGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class DataGenerator {
    @SubscribeEvent
    public void runDataGeneration(GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new BlockModelGenerator(event.getGenerator(), RP.ID, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            final BlockTagGenerator blockTagGenerator = new BlockTagGenerator(
                    event.getGenerator(),
                    RP.ID,
                    event.getExistingFileHelper()
            );
            event.getGenerator().addProvider(blockTagGenerator);
            event.getGenerator().addProvider(new ItemTagGenerator(
                    event.getGenerator(),
                    blockTagGenerator,
                    RP.ID,
                    event.getExistingFileHelper())
            );
            event.getGenerator().addProvider(new BlockEntityTagGenerator(
                    event.getGenerator(),
                    RP.ID,
                    event.getExistingFileHelper()
            ));
        }
    }
}
