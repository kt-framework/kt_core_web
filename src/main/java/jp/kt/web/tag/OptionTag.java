package jp.kt.web.tag;

import org.apache.taglibs.standard.tag.common.core.Util;

/**
 * selectタグ内のoptionタグにselectedを表示するためのタグクラス.
 *
 * @author tatsuya.kumon
 */
public final class OptionTag extends BaseTag {
	/** optionタグの値 */
	private String value;

	/** 選択済みの値 */
	private String selectedValue;

	/** 表示テキスト */
	private String displayText;

	/**
	 * タグ作成.
	 */
	protected String createTag() {
		StringBuilder tag = new StringBuilder();
		tag.append("<option value=\"");
		tag.append(value);
		tag.append("\"");
		// optionタグの値と選択済みの値が等しければselectedを出力
		if (value != null && selectedValue != null
				&& value.equals(selectedValue)) {
			tag.append(" selected");
		}
		tag.append(">");
		// 表示テキスト出力
		tag.append(displayText);
		tag.append("</option>");
		// タグを返す
		return tag.toString();
	}

	/**
	 * 選択済みの値をセット.
	 *
	 * @param selectedValue
	 *            選択済みの値
	 */
	public void setSelectedValue(String selectedValue) {
		this.selectedValue = Util.escapeXml(selectedValue);
	}

	/**
	 * 値をセット.
	 *
	 * @param value
	 *            値
	 */
	public void setValue(String value) {
		this.value = Util.escapeXml(value);
	}

	/**
	 * 表示文字列をセット.
	 *
	 * @param displayText
	 *            表示文字列
	 */
	public void setDisplayText(String displayText) {
		this.displayText = Util.escapeXml(displayText);
	}
}
