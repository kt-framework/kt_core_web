package jp.kt.web.tag;

import java.util.ArrayList;
import java.util.List;

import jp.kt.exception.KtException;
import jp.kt.tool.Validator;
import jp.kt.web.device.Android;
import jp.kt.web.device.Device;
import jp.kt.web.device.Ipad;
import jp.kt.web.device.Iphone;

import org.apache.taglibs.standard.tag.common.core.Util;

/**
 * inputタグクラス.
 * <p>
 * ◆type属性（必須）：inputタイプ<br>
 * 指定可能なタイプは下記の通り.<br>
 * ・text<br>
 * ・email（スマホ専用）<br>
 * ・url（スマホ専用）<br>
 * ・tel（スマホ専用）<br>
 * ・number（スマホ専用）<br>
 * ・search（スマホ専用）<br>
 * ・hidden<br>
 * ・password<br>
 * ・checkbox<br>
 * ・radio<br>
 * ・file<br>
 * ・submit<br>
 * ・image<br>
 * ・reset<br>
 * ・button<br>
 * <br>
 * ◆name属性（必須）：パラメータ名<br>
 * <br>
 * ◆value属性：パラメータ値<br>
 * <br>
 * ◆size属性：サイズ<br>
 * <br>
 * ◆maxlength属性：最大入力可能文字数<br>
 * <br>
 * ◆checkedValue属性：<br>
 * チェック状態にする値.<br>
 * 単一オブジェクト、配列、リストオブジェクトなど全て指定可能.<br>
 * <br>
 * ◆onclick属性：クリックイベント<br>
 * <br>
 * ◆onkeyup属性：キーイベント<br>
 * <br>
 * ◆id属性：スタイルのid指定<br>
 * <br>
 * ◆styleClass属性：スタイルのclass指定<br>
 * <br>
 * ◆style属性：スタイル指定<br>
 * <br>
 * <table border="1" style="width:700px;" summary="タイプ一覧">
 * <tr>
 * <th></th>
 * <th style="width:150px;">タイプ</th>
 * <th style="width:200px;">checkedValue属性</th>
 * <th style="width:200px;">size属性&nbsp;/&nbsp;maxlength属性</th>
 * </tr>
 * <tr>
 * <td rowspan="7">入力系</td>
 * <td>text</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>password</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>email（スマホ専用）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>url（スマホ専用）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>tel（スマホ専用）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>number（スマホ専用）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td>search（スマホ専用）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * </tr>
 * <tr>
 * <td rowspan="2">チェック系</td>
 * <td>checkbox</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>radio</td>
 * <td style="text-align:center;"><span
 * style="font-size:20pt; color:red;">○</span></td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td rowspan="5">ボタン系</td>
 * <td>submit</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>button</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>file</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>image</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>reset</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * <tr>
 * <td>hidden</td>
 * <td>hidden</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * <td style="text-align:center;"><span style="font-size:20pt;">×</span>（使えません）</td>
 * </tr>
 * </table>
 *
 * @author tatsuya.kumon
 */
public final class InputTag extends BaseTag {
	/** タイプ：text */
	private static final String TYPE_TEXT = "text";

	/** タイプ：password */
	private static final String TYPE_PASSWORD = "password";

	/** タイプ：email */
	private static final String TYPE_EMAIL = "email";

	/** タイプ：url */
	private static final String TYPE_URL = "url";

	/** タイプ：tel */
	private static final String TYPE_TEL = "tel";

	/** タイプ：number */
	private static final String TYPE_NUMBER = "number";

	/** タイプ：search */
	private static final String TYPE_SEARCH = "search";

	/** タイプ：hidden */
	private static final String TYPE_HIDDEN = "hidden";

	/** タイプ：checkbox */
	private static final String TYPE_CHECKBOX = "checkbox";

	/** タイプ：radio */
	private static final String TYPE_RADIO = "radio";

	/** タイプ：submit */
	private static final String TYPE_SUBMIT = "submit";

	/** タイプ：button */
	private static final String TYPE_BUTTON = "button";

	/** タイプ：file */
	private static final String TYPE_FILE = "file";

	/** タイプ：image */
	private static final String TYPE_IMAGE = "image";

	/** タイプ：reset */
	private static final String TYPE_RESET = "reset";

	/** type属性 */
	private String type;

	/** パラメータ名 */
	private String name;

	/** パラメータ値 */
	private String value;

	/** テキストボックスのサイズ */
	private String size;

	/** 最大入力可能文字数 */
	private String maxlength;

	/** チェック済みの値 */
	private List<String> checkedValueList;

	/** onclickのjavascript記述 */
	private String onclick;

	/** onkeyupのjavascript記述 */
	private String onkeyup;

	/** id属性 */
	private String id;

	/** styleClass属性 */
	private String styleClass;

	/** style属性 */
	private String style;

	@Override
	protected String createTag() {
		StringBuilder tag = new StringBuilder();
		tag.append("<input");
		// type
		tag.append(" type=\"");
		tag.append(type);
		tag.append("\"");
		// name
		if (!Validator.isEmpty(name)) {
			tag.append(" name=\"");
			tag.append(name);
			tag.append("\"");
		}
		// value
		if (!Validator.isEmpty(value)) {
			tag.append(" value=\"");
			tag.append(value);
			tag.append("\"");
		}
		// checked（checkbox/radioのみ）
		if (type.equals(TYPE_CHECKBOX) || type.equals(TYPE_RADIO)) {
			// パラメータ値とチェック済みの値が等しければcheckedを出力
			if (value != null && checkedValueList != null) {
				if (checkedValueList.contains(value)) {
					tag.append(" checked=\"checked\"");
				}
			}
		}
		// size（text/password/email/url/tel/number/searchのみ）
		// maxlength（text/password/email/url/tel/number/searchのみ）
		if (type.equals(TYPE_TEXT) || type.equals(TYPE_PASSWORD)
				|| type.equals(TYPE_EMAIL) || type.equals(TYPE_URL)
				|| type.equals(TYPE_TEL) || type.equals(TYPE_NUMBER)
				|| type.equals(TYPE_SEARCH)) {
			if (!Validator.isEmpty(size)) {
				tag.append(" size=\"");
				tag.append(size);
				tag.append("\"");
			}
			if (!Validator.isEmpty(maxlength)) {
				tag.append(" maxlength=\"");
				tag.append(maxlength);
				tag.append("\"");
			}
		}
		// id
		if (!Validator.isEmpty(id)) {
			tag.append(" id=\"");
			tag.append(id);
			tag.append("\"");
		}
		// class
		if (!Validator.isEmpty(styleClass)) {
			tag.append(" class=\"");
			tag.append(styleClass);
			tag.append("\"");
		}
		// style
		if (!Validator.isEmpty(style)) {
			tag.append(" style=\"");
			tag.append(style);
			tag.append("\"");
		}
		// onclick
		if (!Validator.isEmpty(onclick)) {
			tag.append(" onclick=\"");
			tag.append(onclick);
			tag.append("\"");
		}
		// onkeyup
		if (!Validator.isEmpty(onkeyup)) {
			tag.append(" onkeyup=\"");
			tag.append(onkeyup);
			tag.append("\"");
		}
		// autocapitalize（iPhone/iPadのみ）
		if (type.equals(TYPE_TEXT) || type.equals(TYPE_EMAIL)
				|| type.equals(TYPE_URL) || type.endsWith(TYPE_SEARCH)) {
			// text/email/url/searchの場合、iPhone/iPadでは先頭大文字にならないようにする
			Device device = super.getDevice();
			if (device instanceof Iphone || device instanceof Ipad) {
				tag.append(" autocapitalize=\"off\"");
			}
		}
		tag.append(">");
		// タグを返す
		return tag.toString();
	}

	/**
	 * タイプをセット.
	 *
	 * @param type
	 *            タイプ
	 */
	public void setType(String type) {
		// 小文字変換
		String t = type.toLowerCase();
		// タイプ設定
		if (t.equals(TYPE_EMAIL) || t.equals(TYPE_URL) || t.equals(TYPE_TEL)
				|| t.equals(TYPE_NUMBER) || t.endsWith(TYPE_SEARCH)) {
			// email/url/tel/number/searchの場合、Androidでは非対応なのでtextに変換する
			Device device = super.getDevice();
			if (device instanceof Android) {
				t = TYPE_TEXT;
			}
		} else if (t.equals(TYPE_TEXT) || t.equals(TYPE_HIDDEN)
				|| t.equals(TYPE_PASSWORD) || t.equals(TYPE_CHECKBOX)
				|| t.equals(TYPE_RADIO) || t.equals(TYPE_FILE)
				|| t.equals(TYPE_SUBMIT) || t.equals(TYPE_IMAGE)
				|| t.equals(TYPE_RESET) || t.equals(TYPE_BUTTON)) {
			// その他対応しているtype
		} else {
			// zooカスタムタグとしては非対応（未確認）のtypeの場合はException
			throw new KtException("A041", "非対応のtypeです [" + t + "]");
		}
		// クラス変数にセット
		this.type = t;
	}

	/**
	 * チェック済みにする値をセット.
	 *
	 * @param checkedValue
	 *            チェック済みにする値
	 */
	public void setCheckedValue(Object checkedValue) {
		if (checkedValue == null) {
			return;
		}
		this.checkedValueList = new ArrayList<String>();
		if (checkedValue instanceof Iterable<?>) {
			// List指定の場合
			for (Object o : (Iterable<?>) checkedValue) {
				if (o != null) {
					this.checkedValueList.add(Util.escapeXml(o.toString()));
				}
			}
		} else if (checkedValue instanceof Object[]) {
			// 配列指定の場合
			for (Object o : (Object[]) checkedValue) {
				if (o != null) {
					this.checkedValueList.add(Util.escapeXml(o.toString()));
				}
			}
		} else {
			// その他のオブジェクトの場合（Stringなど）
			this.checkedValueList.add(Util.escapeXml(checkedValue.toString()));
		}
	}

	/**
	 * 項目名をセット.
	 *
	 * @param name
	 *            項目名
	 */
	public void setName(String name) {
		this.name = Util.escapeXml(name);
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
	 * サイズをセット.
	 *
	 * @param size
	 *            サイズ
	 */
	public void setSize(String size) {
		// 0より大きい整数値の場合のみセットする
		if (!Validator.isEmpty(size) && Validator.isInt(size)
				&& Integer.parseInt(size) > 0) {
			this.size = size;
		}
	}

	/**
	 * 最大長をセット.
	 *
	 * @param maxlength
	 *            最大長
	 */
	public void setMaxlength(String maxlength) {
		// 0より大きい整数値の場合のみセットする
		if (!Validator.isEmpty(maxlength) && Validator.isInt(maxlength)
				&& Integer.parseInt(maxlength) > 0) {
			this.maxlength = maxlength;
		}
	}

	/**
	 * onclick属性をセット.
	 *
	 * @param onclick
	 *            onclick属性値
	 */
	public void setOnclick(String onclick) {
		this.onclick = Util.escapeXml(onclick);
	}

	/**
	 * onkeyup属性をセット.
	 *
	 * @param onkeyup
	 *            onkeyup属性値
	 */
	public void setOnkeyup(String onkeyup) {
		this.onkeyup = Util.escapeXml(onkeyup);
	}

	@Override
	public void setId(String id) {
		this.id = Util.escapeXml(id);
	}

	/**
	 * class属性をセット.
	 *
	 * @param styleClass
	 *            class属性値
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = Util.escapeXml(styleClass);
	}

	/**
	 * style属性をセット.
	 *
	 * @param style
	 *            style属性値
	 */
	public void setStyle(String style) {
		this.style = Util.escapeXml(style);
	}
}
