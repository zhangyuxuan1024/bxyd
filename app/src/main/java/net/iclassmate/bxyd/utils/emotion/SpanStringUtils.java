/**
 *
 */
package net.iclassmate.bxyd.utils.emotion;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import net.iclassmate.bxyd.utils.CharacterParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description :文本中的emojb字符处理为表情图片
 */
public class SpanStringUtils {
    public static String REG_STRING_FOR_AT = "\\[[\\s\\S]+?:[1-9][0-9]?\\{[0-9a-zA-Z]{32}\\}\\]";
    public static String REG_STRING_FOR_SQUAREBRACKET = "\\[[\\s\\S]+?\\]";

    //带表情的字符串
    public static SpannableString getEmotionContent(int emotion_map_type, final Context context, final TextView tv, String source) {
        if (source.contains("[BR/]")) {
            source = source.replaceAll("\\[BR\\/\\]", "");
        }
        if (source.contains("[@") && source.contains(":") && source.contains("{") && source.contains("}]")) {
            if (source.contains("《") && source.contains("》")) {
                source = source.replace("[@", "");
                int start = source.indexOf(":");
                int end = source.indexOf("}]") + 2;
                String sub = source.substring(start, end);
                source = source.replace(sub, "");
            } else {
                source = source.replace("[", "");
                int start = source.indexOf(":");
                int end = source.indexOf("}]") + 2;
                String sub = source.substring(start, end);
                source = source.replace(sub, "");
            }
        }
        if (source.contains("@[") && source.contains(":") && source.contains("{") && source.contains("}]")) {
            source = source.replace("@[", "");
            int start = source.indexOf(":");
            int end = source.indexOf("}]") + 2;
            String sub = source.substring(start, end);
            source = source.replace(sub, "");
        }
        if (source.contains(":") && source.contains("{") && source.contains("}]")) {
            int start = source.indexOf(":");
            int end = source.indexOf("}]") + 2;
            String sub = source.substring(start, end);
            source = source.replace(sub, "");
        }
        if (source.contains("@[")) {
            source = source.replace("@[", "");
        }
        if (source.contains("[") && !source.contains("]")) {
            source = source.replaceAll("\\[", "");
        }
        if (source.contains("&")) {
            source = source.replaceAll("&", "");
        }
        if (source.contains("#")) {
            source = source.replaceAll("#", "");
        }
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w()])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.getImgByName(emotion_map_type, key);
            if (imgRes != null) {
                // 压缩表情图片
                int size = (int) tv.getTextSize() * 13 / 10;
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                if (bitmap != null) {
                    Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    ImageSpan span = new ImageSpan(context, scaleBitmap);
                    spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

    //表情转字符串
    public static String convertToMsg(CharSequence cs, Context mContext) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(cs);
        ImageSpan[] spans = ssb.getSpans(0, cs.length(), ImageSpan.class);
        for (int i = 0; i < spans.length; i++) {
            ImageSpan span = spans[i];
            String c = span.getSource();
            int a = ssb.getSpanStart(span);
            int b = ssb.getSpanEnd(span);
            if (c.contains("emoji")) {
                ssb.replace(a, b, convertUnicode(c));
            }
        }
        ssb.clearSpans();
        return ssb.toString();
    }

    public static String convertUnicode(String emo) {
        emo = emo.substring(emo.indexOf("_") + 1);
        if (emo.length() < 6) {
            return new String(Character.toChars(Integer.parseInt(emo, 16)));
        }
        String[] emos = emo.split("_");
        char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
        char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
        char[] emoji = new char[char0.length + char1.length];
        for (int i = 0; i < char0.length; i++) {
            emoji[i] = char0[i];
        }
        for (int i = char0.length; i < emoji.length; i++) {
            emoji[i] = char1[i - char0.length];
        }
        return new String(emoji);
    }

    public static String convertToMsg2(CharSequence cs, Context mContext) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(cs);
        ImageSpan[] spans = ssb.getSpans(0, cs.length(), ImageSpan.class);
        for (int i = 0; i < spans.length; i++) {
            ImageSpan span = spans[i];
            String c = span.getSource();
            int a = ssb.getSpanStart(span);
            int b = ssb.getSpanEnd(span);
            if (c.contains("emoji")) {
                ssb.replace(a, b, convertUnicode(c));
            }
        }
        ssb.clearSpans();
        return ssb.toString();
    }

    private static String convertUnicode2(String emo) {
        emo = emo.substring(emo.indexOf("_") + 1);
        if (emo.length() < 6) {
            return new String(Character.toChars(Integer.parseInt(emo, 16)));
        }
        String[] emos = emo.split("_");
        char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
        char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
        char[] emoji = new char[char0.length + char1.length];
        for (int i = 0; i < char0.length; i++) {
            emoji[i] = char0[i];
        }
        for (int i = char0.length; i < emoji.length; i++) {
            emoji[i] = char1[i - char0.length];
        }
        return new String(emoji);
    }
}
