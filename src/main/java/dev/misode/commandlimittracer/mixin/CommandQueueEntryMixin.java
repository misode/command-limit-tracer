package dev.misode.commandlimittracer.mixin;

import dev.misode.commandlimittracer.Traceable;
import net.minecraft.commands.execution.CommandQueueEntry;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandQueueEntry.class)
public class CommandQueueEntryMixin implements Traceable {
    @Unique
    public @Nullable String traceInfo;

    @Override
    public void commandlimittracer$setTraceInfo(String trace) {
        this.traceInfo = trace;
    }

    @Override
    public String commandlimittracer$getTraceInfo() {
        return this.traceInfo;
    }
}
