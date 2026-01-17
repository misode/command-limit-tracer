package dev.misode.commandlimittracer;

public record Trace(int depth, String info) {
    @Override
    public String toString() {
        return this.info;
    }
}
