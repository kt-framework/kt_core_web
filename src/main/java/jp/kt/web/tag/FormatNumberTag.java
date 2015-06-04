package jp.kt.web.tag;

import java.text.DecimalFormat;

import jp.kt.tool.Validator;

/**
 * 数値をフォーマットして表示するためのタグクラス.
 *
 * @author tatsuya.kumon
 */
public final class FormatNumberTag extends BaseTag {
	/** 数値 */
	private long value;

	/** パターン */
	private String pattern;

	/**
	 * タグ作成.
	 */
	protected String createTag() {
		// 数値をフォーマットする
		DecimalFormat format = new DecimalFormat();
		if (!Validator.isEmpty(pattern)) {
			format = new DecimalFormat(pattern);
		}
		String dateText = format.format(value);
		// タグを返す
		return dateText;
	}

	/**
	 * フォーマットをセット.
	 *
	 * @param pattern
	 *            フォーマット
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 数値をセット.
	 *
	 * @param value
	 *            数値
	 */
	public void setValue(long value) {
		this.value = value;
	}
}
