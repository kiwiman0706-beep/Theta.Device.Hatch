package jp.yaegaki.equipment;
import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.RobolectricTestRunner;import org.robolectric.annotation.*;import static org.junit.Assert.*;import java.util.*;import android.graphics.Bitmap;import com.google.zxing.*;import com.google.zxing.common.HybridBinarizer;
@RunWith(RobolectricTestRunner.class) @Config(sdk=34) @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class LabelTest {
 @Test public void renderedQrCanBeDecoded()throws Exception{String url="http://192.168.24.10/admin";Bitmap b=Labels.render(Arrays.asList("受付PC","MAC: AA:BB:CC:DD:EE:FF","設置場所: 受付"),url,288,360);int[] pixels=new int[b.getWidth()*b.getHeight()];b.getPixels(pixels,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());BinaryBitmap binary=new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(b.getWidth(),b.getHeight(),pixels)));assertEquals(url,new MultiFormatReader().decode(binary).getText());}
 @Test public void preventTinyQrAndText()throws Exception{try{Labels.render(Arrays.asList("機器"),"https://example.com/very/long/url/that/needs/a/bigger/label/123456789012345678901234567890",80,360);fail();}catch(Exception expected){}try{Labels.render(Collections.nCopies(20,"機器"),"",144,360);fail();}catch(Exception expected){}}
}
