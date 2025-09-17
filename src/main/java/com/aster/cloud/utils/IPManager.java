package com.aster.cloud.utils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * IP 管理工具：检测 IPv4/IPv6 / CIDR，IP 等值与是否属于某个网段（支持 IPv4-mapped IPv6）
 */
public class IPManager {

    // 简单判断字符串是否包含 IPv4/IPv6 特征，避免对 hostname 做 DNS 解析
    private static boolean looksLikeIPv4(String s) {
        return s != null && s.indexOf('.') >= 0;
    }

    private static boolean looksLikeIPv6(String s) {
        return s != null && s.indexOf(':') >= 0;
    }

    /** 判断是否为 IPv4 文本表示 */
    public static boolean isIPv4(String s) {
        if (!looksLikeIPv4(s)) return false;
        try {
            InetAddress addr = InetAddress.getByName(s);
            return addr instanceof Inet4Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /** 判断是否为 IPv6 文本表示 */
    public static boolean isIPv6(String s) {
        if (!looksLikeIPv6(s)) return false;
        try {
            InetAddress addr = InetAddress.getByName(s);
            return addr instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /** 判断是否为 IPv4 CIDR（x.x.x.x/0..32） */
    public static boolean isIPv4Cidr(String cidr) {
        if (cidr == null) return false;
        String[] parts = cidr.split("/", -1);
        if (parts.length != 2) return false;
        if (!isIPv4(parts[0])) return false;
        try {
            int prefix = Integer.parseInt(parts[1]);
            return prefix >= 0 && prefix <= 32;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** 判断是否为 IPv6 CIDR（addr/prefix 0..128） */
    public static boolean isIPv6Cidr(String cidr) {
        if (cidr == null) return false;
        String[] parts = cidr.split("/", -1);
        if (parts.length != 2) return false;
        if (!isIPv6(parts[0])) return false;
        try {
            int prefix = Integer.parseInt(parts[1]);
            return prefix >= 0 && prefix <= 128;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** 是否为单个 IP（v4 或 v6） */
    public static boolean isSingleIp(String s) {
        return isIPv4(s) || isIPv6(s);
    }

    /** 是否为 CIDR（v4 或 v6） */
    public static boolean isCidr(String s) {
        return isIPv4Cidr(s) || isIPv6Cidr(s);
    }

    /**
     * 判断两个字符串表示的地址是否表示“相同地址”。
     * 处理 IPv4、IPv6，以及 IPv4-mapped IPv6（::ffff:a.b.c.d）情况。
     */
    public static boolean equalsIp(String a, String b) {
        if (a == null || b == null) return false;
        try {
            InetAddress A = InetAddress.getByName(a);
            InetAddress B = InetAddress.getByName(b);
            InetAddress nA = normalizeToComparable(A);
            InetAddress nB = normalizeToComparable(B);
            return Arrays.equals(nA.getAddress(), nB.getAddress());
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 判断 ip 是否属于 cidr（支持 IPv4/IPv6；支持 IPv4-mapped IPv6 与 IPv4 网段互相匹配）
     *
     * 返回 false 的情形包括：cidr 或 ip 非法，地址族不兼容且无法映射等。
     */
    public static boolean ipInCidr(String ip, String cidr) {
        if (ip == null || cidr == null) return false;
        String[] parts = cidr.split("/", -1);
        if (parts.length != 2) return false;
        int prefix;
        try {
            prefix = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            InetAddress networkAddr = InetAddress.getByName(parts[0]);
            InetAddress targetAddr = InetAddress.getByName(ip);

            // 处理 IPv4 <-> IPv6-mapped 互转，以便比较：
            if (networkAddr instanceof Inet4Address && targetAddr instanceof Inet6Address) {
                // 如果 target 是 IPv4-mapped IPv6，把它转为 IPv4
                byte[] tbytes = targetAddr.getAddress();
                if (isIPv4Mapped(tbytes)) {
                    targetAddr = InetAddress.getByAddress(Arrays.copyOfRange(tbytes, 12, 16));
                } else {
                    // IPv6 非映射形式，不属于 IPv4 网段
                    return false;
                }
            } else if (networkAddr instanceof Inet6Address && targetAddr instanceof Inet4Address) {
                // 把 IPv4 target 转为 IPv4-mapped IPv6，与 IPv6 network 比较（合理当你把 IPv4 放到 IPv6 空间）
                byte[] mapped = new byte[16];
                // 前 10 字节 0
                // bytes 10-11 = 0xff
                // last 4 bytes = ipv4 bytes
                byte[] tbytes = targetAddr.getAddress();
                mapped[10] = (byte) 0xff;
                mapped[11] = (byte) 0xff;
                System.arraycopy(tbytes, 0, mapped, 12, 4);
                targetAddr = InetAddress.getByAddress(mapped);
            }

            byte[] netBytes = networkAddr.getAddress();
            byte[] ipBytes = targetAddr.getAddress();

            // prefix 合法性
            int bits = netBytes.length * 8;
            if (prefix < 0 || prefix > bits) return false;

            int fullBytes = prefix / 8;
            int remainingBits = prefix % 8;

            // 完整字节逐字节比较（用无符号比对）
            for (int i = 0; i < fullBytes; i++) {
                if ((netBytes[i] & 0xFF) != (ipBytes[i] & 0xFF)) return false;
            }

            if (remainingBits > 0) {
                int mask = ((0xFF) << (8 - remainingBits)) & 0xFF;
                if (((netBytes[fullBytes] & 0xFF) & mask) != ((ipBytes[fullBytes] & 0xFF) & mask)) {
                    return false;
                }
            }

            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    // ---------------- helper ----------------

    /** 判断一个 InetAddress 是否是 IPv4-mapped IPv6（::ffff:a.b.c.d） */
    private static boolean isIPv4Mapped(byte[] b) {
        if (b == null || b.length != 16) return false;
        for (int i = 0; i < 10; i++) if (b[i] != 0) return false;
        return (b[10] == (byte) 0xff && b[11] == (byte) 0xff);
    }

    /**
     * 归一化地址用于比较：
     * - 如果是 IPv4-mapped IPv6，返回对应的 IPv4 InetAddress（4 byte）
     * - 否则返回原始地址（不改变）
     */
    private static InetAddress normalizeToComparable(InetAddress addr) throws UnknownHostException {
        if (addr instanceof Inet6Address) {
            byte[] b = addr.getAddress();
            if (isIPv4Mapped(b)) {
                // 取最后 4 字节构造 IPv4
                return InetAddress.getByAddress(Arrays.copyOfRange(b, 12, 16));
            }
        }
        return addr;
    }

}
