package com.inspur.ftpparserframework.license;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Properties;

import com.inspur.ftpparserframework.util.TimeUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class SignProvider
{
	private static String LICENSE_PATH = "license.properties";
	private static String PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCY4nehPK13F+/lEdPTS3JzlgRLXTWfe/7tvv2Jkz4btXtqL51rkObhOGxAr1WUMVfPpICqKLCqXbdBWzLZLkIJhgCccPHiL7EaV/U9SlhQvcTbfOm9yzoRn5uEXgEaSTrJalgDpzANJo/Gkk/G8IMBrJzsI3v54D/J1u8YKCA5nwIDAQAB";

	private static boolean verify(byte[] pubKeyText, String plainText, byte[] signText)
	{
		try
		{
			// 解密由base64编码的公钥,并构造X509EncodedKeySpec对象
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(new String(pubKeyText)));
			// RSA对称加密算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// 取公钥匙对象
			PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
			// 解密由base64编码的数字签名
			byte[] signed = Base64.decode(new String(signText));
			Signature signatureChecker = Signature.getInstance("MD5withRSA");
			signatureChecker.initVerify(pubKey);
			signatureChecker.update(plainText.getBytes());
			// 验证签名是否正常
			if (signatureChecker.verify(signed))
				return true;
			else
				return false;
		} catch (Throwable e)
		{
			System.out.println("校验签名失败");
			e.printStackTrace();
			return false;
		}
	}

	public static Boolean isLicense()
	{
		boolean isLicense = false;
		InputStream in;
		try
		{
			String filepath = Thread.currentThread().getContextClassLoader().getResource(LICENSE_PATH).toURI()
					.getPath();
			in = new BufferedInputStream(new FileInputStream(filepath));
			Properties p = new Properties();
			p.load(in);
			String expiryDate = (String) p.get("expiryDate");
			String signed = (String) p.get("signed");
			isLicense = SignProvider.verify(PUBKEY.getBytes(), expiryDate, signed.getBytes());
			if (isLicense)
			{
				long now = TimeUtil.getToday();
				Date expiry = TimeUtil.str2date(expiryDate, "yyyyMMdd");
				if (expiry.getTime() < now)
				{
					System.out.println("授权过期！");
					isLicense = false;
				}
			} else
			{
				System.out.println("授权无效或已经过期！");
			}
		} catch (Exception e)
		{
			isLicense = false;
			System.out.println("未找到授权文件！");
		}
		return isLicense;
	}

	public static void main(String[] args)
	{
		System.out.println(SignProvider.isLicense());
	}

}
