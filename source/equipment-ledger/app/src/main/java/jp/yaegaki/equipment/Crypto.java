package jp.yaegaki.equipment;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
public final class Crypto {
 public static byte[] encrypt(byte[] data, javax.crypto.SecretKey key) throws Exception {
  Cipher c=Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.ENCRYPT_MODE,key); byte[] enc=c.doFinal(data); byte[] out=new byte[12+enc.length];System.arraycopy(c.getIV(),0,out,0,12);System.arraycopy(enc,0,out,12,enc.length);return out;
 }
 public static byte[] decrypt(byte[] data, javax.crypto.SecretKey key) throws Exception {
  if(data.length<28)throw new Exception("暗号化データが不正です");Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,key,new GCMParameterSpec(128,data,0,12));return c.doFinal(data,12,data.length-12);
 }
 public static javax.crypto.SecretKey passwordKey(char[] password,byte[] salt)throws Exception {
  PBEKeySpec spec=new PBEKeySpec(password,salt,210000,256);try{return new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(),"AES");}finally{spec.clearPassword();}
 }
}
