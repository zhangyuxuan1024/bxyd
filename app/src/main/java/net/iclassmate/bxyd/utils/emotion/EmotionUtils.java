
package net.iclassmate.bxyd.utils.emotion;

import android.support.v4.util.ArrayMap;

import net.iclassmate.bxyd.R;




/**
 * @description :表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
 */
public class EmotionUtils {

	/**
	 * 表情类型标志符
	 */
	public static final int EMOTION_CLASSIC_TYPE=0x0001;//经典表情

	/**
	 * key-表情文字;
	 * value-表情图片资源
	 */
	public static ArrayMap<String, Integer> EMPTY_MAP;
	public static ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;


	static {
		EMPTY_MAP = new ArrayMap<>();
		EMOTION_CLASSIC_MAP = new ArrayMap<>();

		EMOTION_CLASSIC_MAP.put("[笑]", R.mipmap.ic_emoticons13);
		EMOTION_CLASSIC_MAP.put("[苦笑]",  R.mipmap.ic_emoticons8);
		EMOTION_CLASSIC_MAP.put("[酷]", R.mipmap.ic_emoticons6);
		EMOTION_CLASSIC_MAP.put("[色]", R.mipmap.ic_emoticons5);
		EMOTION_CLASSIC_MAP.put("[难过]",  R.mipmap.ic_emoticons18);
		EMOTION_CLASSIC_MAP.put("[淘气]", R.mipmap.ic_emoticons1);
		EMOTION_CLASSIC_MAP.put("[傲慢]", R.mipmap.ic_emoticons7);
		EMOTION_CLASSIC_MAP.put("[生气]",  R.mipmap.ic_emoticons4);
		EMOTION_CLASSIC_MAP.put("[委屈]",  R.mipmap.ic_emoticons17);
		EMOTION_CLASSIC_MAP.put("[哭]", R.mipmap.ic_emoticons9);
		EMOTION_CLASSIC_MAP.put("[饿]", R.mipmap.ic_emoticons3);
		EMOTION_CLASSIC_MAP.put("[哈欠]", R.mipmap.ic_emoticons11);
		EMOTION_CLASSIC_MAP.put("[惊恐]",  R.mipmap.ic_emoticons10);
		EMOTION_CLASSIC_MAP.put("[惊讶]", R.mipmap.ic_emoticons2);
		EMOTION_CLASSIC_MAP.put("[难过(严谨)]", R.mipmap.ic_emoticons16);
		EMOTION_CLASSIC_MAP.put("[笑(严谨)]",  R.mipmap.ic_emoticons15);
		EMOTION_CLASSIC_MAP.put("[住嘴]",  R.mipmap.ic_emoticons12);
		EMOTION_CLASSIC_MAP.put("[同意]",  R.mipmap.ic_emoticons19);
		EMOTION_CLASSIC_MAP.put("[不同意]",  R.mipmap.ic_emoticons20);
		EMOTION_CLASSIC_MAP.put("[OK]",  R.mipmap.ic_ic_emoticons14);


	}

	/**
	 * 根据名称获取当前表情图标R值
	 * @param EmotionType 表情类型标志符
	 * @param imgName 名称
	 * @return
	 */
	public static int getImgByName(int EmotionType,String imgName) {
		Integer integer=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				integer = EMOTION_CLASSIC_MAP.get(imgName);
				break;
			default:
//				LogUtils.e("the emojiMap is null!!");
				break;
		}
		return integer == null ? -1 : integer;
	}

	/**
	 * 根据类型获取表情数据
	 * @param EmotionType
	 * @return
	 */
	public static ArrayMap<String, Integer> getEmojiMap(int EmotionType){
		ArrayMap EmojiMap=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				EmojiMap=EMOTION_CLASSIC_MAP;
				break;
			default:
				EmojiMap=EMPTY_MAP;
				break;
		}
		return EmojiMap;
	}
}
