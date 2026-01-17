package dev.misode.commandlimittracer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.misode.commandlimittracer.Traceable;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.execution.tasks.ContinuationTask;
import net.minecraft.commands.functions.InstantiatedFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(CallFunction.class)
public class CallFunctionMixin {
    @Shadow
    @Final
    private InstantiatedFunction<?> function;

    @ModifyArg(method = "execute(Lnet/minecraft/commands/ExecutionCommandSource;Lnet/minecraft/commands/execution/ExecutionContext;Lnet/minecraft/commands/execution/Frame;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/execution/tasks/ContinuationTask;schedule(Lnet/minecraft/commands/execution/ExecutionContext;Lnet/minecraft/commands/execution/Frame;Ljava/util/List;Lnet/minecraft/commands/execution/tasks/ContinuationTask$TaskProvider;)V"), index = 3)
    private ContinuationTask.TaskProvider<Object, Object> wrapEntry(ContinuationTask.TaskProvider<Object, Object> taskProvider, @Local List<UnboundEntryAction<?>> list) {
        return (a, b) -> {
            CommandQueueEntry<Object> entry = taskProvider.create(a, b);
            if (b == list.getFirst()) {
                ((Traceable)(Object)entry).commandlimittracer$setTraceInfo(this.function.id().toString());
            }
            return entry;
        };
    }
}
