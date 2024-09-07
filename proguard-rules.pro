# Keep Minecraft and Forge classes to avoid issues with core functionality
-keep public class net.minecraft.** { *; }
-keep public class net.minecraftforge.** { *; }

# Keep the class names but obfuscate internal methods and fields
-keep class org.brutality.Main {
    public *;
    protected *;
}
-keep class org.brutality.module.Module {
    public *;
    protected *;
}
-keep class org.brutality.commands.CommandManager {
    public *;
    protected *;
}

# Obfuscate internal code of the Main, Module, and CommandManager but retain method signatures for access
-keepclassmembers class org.brutality.Main {
    !private *;
}
-keepclassmembers class org.brutality.module.Module {
    !private *;
}
-keepclassmembers class org.brutality.commands.CommandManager {
    !private *;
}

# Allow ProGuard to obfuscate code inside these classes, but keep method/field signatures for accessibility
# This protects the code but doesn't break the mod functionality.
# Note: The `-fieldobfuscationdictionary` and similar options are removed as they're unsupported.

# Enable aggressive obfuscation while preserving access to required members
-allowaccessmodification
-overloadaggressively
-adaptclassstrings

# Suppress warnings about missing classes or incomplete hierarchies
-dontwarn org.brutality.**
-ignorewarnings

# Remove debug information to prevent easier reverse engineering
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontpreverify
-dontnote
