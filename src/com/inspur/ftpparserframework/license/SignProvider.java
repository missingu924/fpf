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
			// ������base64����Ĺ�Կ,������X509EncodedKeySpec����
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(new String(pubKeyText)));
			// RSA�ԳƼ����㷨
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// ȡ��Կ�׶���
			PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
			// ������base64���������ǩ��
			byte[] signed = Base64.decode(new String(signText));
			Signature signatureChecker = Signature.getInstance("MD5withRSA");
			signatureChecker.initVerify(pubKey);
			signatureChecker.update(plainText.getBytes());
			// ��֤ǩ���Ƿ�����
			if (signatureChecker.verify(signed))
				return true;
			else
				return false;
		} catch (Throwable e)
		{
			System.out.println("У��ǩ��ʧ��");
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
					System.out.println("��Ȩ���ڣ�");
					isLicense = false;
				}
			} else
			{
				System.out.println("��Ȩ��Ч���Ѿ����ڣ�");
			}
		} catch (Exception e)
		{
			isLicense = false;
			System.out.println("δ�ҵ���Ȩ�ļ���");
		}
		return isLicense;
	}

	public static void main(String[] args)
	{
		System.out.println(SignProvider.isLicense());
	}

}
