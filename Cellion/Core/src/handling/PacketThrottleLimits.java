package handling;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotations to define within the packet handler how much packet can the server receive within a specific amount of time. This is so to
 * prevent spam/denial of service.
 *
 * @author Lloyd Korn
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketThrottleLimits {

    /**
     * The amount of packet sent within x time, to flag red
     *
     * @return
     */
    int FlagCount() default 0;

    /**
     * The amount of time in millis to reset the packet sent cound
     *
     * @return
     */
    int ResetTimeMillis() default 0;

    /**
     * The min time that the packet can be sent.
     *
     * @return
     */
    int MinTimeMillisBetweenPackets() default 0;

    /**
     * To aid debugging, even when the .jar binary is obfuscated
     *
     * @return
     */
    String FunctionName() default "";

    PacketThrottleBanType BanType() default PacketThrottleBanType.None;

    public enum PacketThrottleBanType {
        PermanentBan,
        Disconnect,
        None
    }
}
