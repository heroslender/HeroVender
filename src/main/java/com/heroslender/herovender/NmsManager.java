package com.heroslender.herovender;

import com.heroslender.herostackdrops.nms.ItemStackDeserializer;
import com.heroslender.herostackdrops.nms.Nms;
import com.heroslender.herovender.version.NMSVersion;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class NmsManager {
    @Nullable
    @Getter
    private final ItemStackDeserializer deserializer;

    @Nullable
    @Getter
    private final Nms nms;

    public NmsManager() {
        NMSVersion currentVersion = NMSVersion.CURRENT;
        if (NMSVersion.V1_8_R3.equals(currentVersion)) {
            deserializer = new com.heroslender.herovender.nms.v1_8_R3.ItemStackDeserializerImpl();
            nms = new com.heroslender.herovender.nms.v1_8_R3.NmsImpl();
        } else if (NMSVersion.V1_13_R1.isLowerThanOrEqualTo(currentVersion)) {
            deserializer = new com.heroslender.herovender.nms.v1_13_R1.ItemStackDeserializerImpl();
            nms = new com.heroslender.herovender.nms.v1_13_R1.NmsImpl();
        } else {
            deserializer = null;
            nms = null;
        }
    }
}
