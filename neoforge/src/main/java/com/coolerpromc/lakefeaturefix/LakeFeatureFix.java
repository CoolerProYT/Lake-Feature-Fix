package com.coolerpromc.lakefeaturefix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MODID)
public class LakeFeatureFix {
    public LakeFeatureFix(IEventBus eventBus) {
        CommonClass.init();
    }
}