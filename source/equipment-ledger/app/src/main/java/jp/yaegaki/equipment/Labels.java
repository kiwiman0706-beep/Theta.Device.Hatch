package jp.yaegaki.equipment;
import android.graphics.*;import com.google.zxing.*;import com.google.zxing.common.*;import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;import java.util.*;
public final class Labels {
 public static Bitmap render(List<String> lines,String url,int height,int dpi)throws Exception{
  if(lines.isEmpty()&&url.isEmpty())throw new Exception("印刷する項目を選択してください");
  int pad=Math.max(8,dpi/30),size=Math.min(dpi/9,(height-pad*2)/Math.max(1,lines.size()));
  if(!lines.isEmpty()&&size<dpi/18)throw new Exception("文字が小さくなりすぎます。項目を減らすか幅の広いテープを選んでください。");
  Paint text=new Paint(Paint.ANTI_ALIAS_FLAG);text.setColor(Color.BLACK);text.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));text.setTextSize(size);
  int textWidth=0;for(String s:lines)textWidth=Math.max(textWidth,(int)Math.ceil(text.measureText(s)));
  int qrW=0;BitMatrix qr=null;int scale=0;
  if(!url.isEmpty()){if(url.length()>500)throw new Exception("QRのURLが長すぎます（500文字以内）");Map<EncodeHintType,Object> hints=new EnumMap<>(EncodeHintType.class);hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.M);hints.put(EncodeHintType.MARGIN,4);qr=new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE,1,1,hints);scale=(height-2*pad)/qr.getHeight();if(scale<3)throw new Exception("QRコードが細かすぎます。テープ幅を広げるかURLを短くしてください。");qrW=qr.getWidth()*scale;}
  int w=pad*2+textWidth+(qrW>0?qrW+(textWidth>0?pad:0):0);if(w>dpi*12)throw new Exception("ラベルが長すぎます。メモなどの項目を減らしてください。");
  Bitmap bmp=Bitmap.createBitmap(w,height,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(bmp);c.drawColor(Color.WHITE);int x=pad;
  if(qr!=null){Paint p=new Paint();p.setColor(Color.BLACK);int y=(height-qrW)/2;for(int yy=0;yy<qr.getHeight();yy++)for(int xx=0;xx<qr.getWidth();xx++)if(qr.get(xx,yy))c.drawRect(x+xx*scale,y+yy*scale,x+(xx+1)*scale,y+(yy+1)*scale,p);x+=qrW+pad;}
  float lh=(height-2f*pad)/Math.max(1,lines.size());Paint.FontMetrics fm=text.getFontMetrics();for(int i=0;i<lines.size();i++){float y=pad+i*lh+(lh-fm.bottom-fm.top)/2;c.drawText(lines.get(i),x,y,text);}return bmp;
 }
}
