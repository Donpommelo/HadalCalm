# proguard github example stuff for application
# https://github.com/Guardsquare/proguard/blob/master/examples/gradle/applications.gradle

-verbose

-keeppackagenames java.**,java.security.**,java.lang.reflect.**

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes Signature,InnerClasses,SourceFile,LineNumberTable

-dontwarn com.badlogic.*
-dontwarn org.lwjgl.*
-dontwarn org.objectweb.*
-dontwarn com.esotericsoftware.*

-keep class com.badlogic.*
-keep class org.lwjgl.*
-keep class org.objectweb.*
-keep class com.esotericsoftware.*

-keepclassmembers class com.badlogic.* { *; }
-keepclassmembers class org.lwjgl.* { *; }
-keepclassmembers class org.objectweb.* { *; }
-keepclassmembers class com.esotericsoftware.* { *; }

-keep class * implements com.badlogic.gdx.utils.Json*

-keep class com.mygdx.hadal.save.InfoItem* { <fields>; }
-keep class com.mygdx.hadal.save.UnlockActives* { <fields>; }
-keep class com.mygdx.hadal.save.UnlockArtifact* { <fields>; }
-keep class com.mygdx.hadal.save.UnlockCharacter* { <fields>; }
-keep class com.mygdx.hadal.save.UnlockEquip* { <fields>; }
-keep class com.mygdx.hadal.save.UnlockLevel* { <fields>; }
-keep class com.mygdx.hadal.dialog.DialogInfo* { <fields>; }
-keep class com.mygdx.hadal.dialog.DeathMessage* { <fields>; }
-keep public class * {
    public protected *;
}

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Signature,Exceptions,*Annotation*,
                InnerClasses,PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable

-keepclasseswithmembers public class * { public static void main(java.lang.String[]); }

-keepclasseswithmembernames,includedescriptorclasses class java.lang.* { native <methods>; }

-keepclasseswithmembernames,includedescriptorclasses class * { native <methods>; }

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.mygdx.hadal.desktop.DesktopLauncher.* {
  public static void main(java.lang.String[]);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Box2d
-keepclassmembers class com.badlogic.gdx.physics.box2d.World* {
   boolean contactFilter(long, long);
   void    beginContact(long);
   void    endContact(long);
   void    preSolve(long, long);
   void    postSolve(long, long);
   boolean reportFixture(long);
   float   reportRayFixture(long, float, float, float, float, float);
}

# Kryo
-dontwarn sun.reflect.*
-dontwarn java.beans.*
-dontwarn sun.nio.ch.*
-dontwarn sun.misc.*

-keep class com.esotericsoftware.kryo.* {*;}
-keep class com.esotericsoftware.* {*;}

-keep class java.beans.* { *; }
-keep class sun.reflect.* { *; }
-keep class sun.nio.ch.* { *; }

-forceprocessing
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#-classobfuscationdictionary 'obfuscationClassNames.txt'
-ignorewarnings
-mergeinterfacesaggressively
-repackageclasses ''
-allowaccessmodification

-optimizations !code/allocation/variable

# FIELD ISSUE NPE
-optimizations !field/propagation/value

# ColorTools fix
-optimizations !code/simplification/string

###### PROGUARD ANNOTATIONS END #####
-optimizationpasses 5