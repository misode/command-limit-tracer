package dev.misode.commandlimittracer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.misode.commandlimittracer.Trace;
import dev.misode.commandlimittracer.Traceable;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.ExecutionContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(ExecutionContext.class)
public class ExecutionContextMixin {
    @Unique
    private final ArrayList<Trace> traces = new ArrayList<>();

    @WrapOperation(method = "runCommandQueue", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void reportCommandLimit(Logger logger, String message, Object commandLimit, Operation<Void> original) {
        original.call(logger, message, commandLimit);
        if (traces.size() > 10) {
            traces.subList(traces.size() - 5, traces.size()).reversed().forEach(trace -> logger.info("  {}", trace));
            logger.info("  ({} hidden calls)", traces.size() - 10);
            traces.subList(0, 5).reversed().forEach(trace -> logger.info("  {}", trace));
        } else {
            traces.reversed().forEach(trace -> logger.info("  {}", trace));
        }
    }

    @WrapOperation(method = "runCommandQueue", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/execution/CommandQueueEntry;execute(Lnet/minecraft/commands/execution/ExecutionContext;)V"))
    private void executeQueueEntry(CommandQueueEntry<?> entry, ExecutionContext<?> executionContext, Operation<Void> original) {
        String info = ((Traceable)(Object)entry).commandlimittracer$getTraceInfo();
        if (info != null) {
            this.pushTrace(entry.frame().depth(), info);
        }
        original.call(entry, executionContext);
    }

    @Unique
    private void pushTrace(int depth, String info) {
        traces.removeIf(trace -> trace.depth() >= depth);
        traces.add(new Trace(depth, info));
    }
}
