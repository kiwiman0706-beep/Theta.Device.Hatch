package jp.yaegaki.equipment;
import java.util.*;import java.util.regex.*;
public final class Extract {
 public static LinkedHashMap<String,String> fields(String raw){
 String text=java.text.Normalizer.normalize(raw,java.text.Normalizer.Form.NFKC);LinkedHashMap<String,String> out=new LinkedHashMap<>();
 Matcher m=Pattern.compile("(?i)(?<![0-9a-f])(?:[0-9a-f]{2}[:-]){5}[0-9a-f]{2}(?![0-9a-f])").matcher(text); LinkedHashSet<String> macs=new LinkedHashSet<>();while(m.find())macs.add(m.group().replace('-',':').toUpperCase(Locale.ROOT));
 if(macs.isEmpty()){m=Pattern.compile("(?i)(?:MAC(?:\\s*(?:ADDRESS|アドレス))?)[\\s:：=]*([0-9a-f]{12})(?![0-9a-f])").matcher(text);while(m.find()){String s=m.group(1).toUpperCase(Locale.ROOT);macs.add(s.replaceAll("(..)(?!$)","$1:"));}}
 if(!macs.isEmpty())out.put("mac",String.join("\n",macs));
 m=Pattern.compile("(?<![0-9.])(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?![0-9.])").matcher(text);LinkedHashSet<String> ips=new LinkedHashSet<>();while(m.find())if(validIP(m.group()))ips.add(m.group());if(!ips.isEmpty())out.put("ip",String.join("\n",ips));
 for(String maker:new String[]{"BUFFALO","ELECOM","TP-Link","IODATA","I-O DATA","Synology","QNAP","NEC","FUJITSU","DELL","Lenovo","HP","EPSON","Canon","Brother","KING JIM","Panasonic","ASUS","Acer","Apple","Microsoft","NETGEAR","YAMAHA","Hikvision","Tapo","Ctronics"})if(Pattern.compile("(?i)(?<![a-z])"+Pattern.quote(maker)+"(?![a-z])").matcher(text).find()){out.put("maker",maker);break;}
 m=Pattern.compile("(?im)(?:MODEL(?:\\s*(?:NO\\.?|NUMBER))?|型番|型名|品番)[ \\t:：=]+([^\\r\\n]+)").matcher(text);if(m.find())out.put("model",m.group(1).trim());
 m=Pattern.compile("(?im)(?:S/N|SERIAL(?:\\s*(?:NO\\.?|NUMBER))?|製造番号)[ \\t:：=]+([^\\r\\n]+)").matcher(text);if(m.find())out.put("serial",m.group(1).trim());
 m=Pattern.compile("https?://[^\\s<>\"へ]+").matcher(text);if(m.find())out.put("url",m.group());return out;
 }
 public static boolean validIP(String s){if(!s.matches("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}"))return false;for(String v:s.split("\\."))if(Integer.parseInt(v)>255)return false;return true;}
 public static boolean validMac(String s){return s.matches("(?i)(?:[0-9a-f]{2}:){5}[0-9a-f]{2}");}
}
