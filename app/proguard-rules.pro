-optimizationpasses 7
-allowaccessmodification

#-keep public class * extends android.app.Fragment
-keep public class android.** { *;}
#-keep public class de.robv.** {
#    !private *;
#}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    native <methods>;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-dontwarn *
-repackageclasses net_62v.meta
