package com.ego.auth;

import com.ego.auth.entity.UserInfo;
import com.ego.auth.utils.JwtUtils;
import com.ego.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public class JwtTest {

    private static final String pubKeyPath = "E:\\workspace\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\workspace\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;
    /**
     * 根据秘钥生成公钥和私钥
     * 注释@Before代码运行
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "qwe123!@#");
    }

    /**
     * 根据路径获取私钥和公钥
     * @throws Exception
     */
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 解密token
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU1NjE2NjYyMn0.VpgMugC0b9dtTSTlsjW8uBnZmzItvhumA3dTAqSrPOJs4jFsU9z9f7Y8-mGYKL6YK4SlGWWwAVQT14E-lF90d0L0gUODLsv0C7StF8F0HYTdfwgWlB2-GJHFG8ebMbeHYCxW1Fu8IoIB9EnjMv1BgjdNkoKGYCN_0E33wQHHKlU";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
