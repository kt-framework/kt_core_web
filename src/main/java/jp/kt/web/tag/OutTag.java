package jp.kt.web.tag;

import jp.kt.tool.HtmlUtil;
import jp.kt.tool.StringUtil;

/**
 * テキスト出力タグクラス.
 * <p>
 * 下記の機能を提供する.<br>
 * ・HTML表示のためのエスケープ<br>
 * ・改行コードを＜br＞タグへ変換<br>
 * </p>
 *
 * @author tatsuya.kumon
 */
public final class OutTag extends BaseTag {
	/**
	 * タグ属性 value
	 */
	private String value;

	/**
	 * タグ属性 escapeXml.<br>
	 * デフォルトtrue.
	 */
	private boolean escapeXml = true;

	/**
	 * タグ属性 convertBR.<br>
	 * デフォルトfalse
	 */
	private boolean convertBR = false;

	/**
	 * タグ属性 convertKana.<br>
	 * trueだと全角カナを半角カナへ変換する.<br>
	 * デフォルトfalse
	 */
	private boolean zenToHan = false;

	/**
	 * タグ属性 cutByte.<br>
	 * 指定バイト数で文字列をカットする.<br>
	 * デフォルトnull
	 */
	private Integer cutByte = null;

	/**
	 * タグ作成.
	 */
	protected String createTag() {
		String out = new String(value);
		// エスケープ処理
		if (escapeXml) {
			// HTML特殊文字をエスケープする
			out = HtmlUtil.escape(out);
		}
		// 改行コードを<br>タグに変換
		if (convertBR) {
			out = HtmlUtil.replaceLineToBrtag(out);
		}
		// 全角文字を半角文字へ変換
		if (zenToHan) {
			out = StringUtil.zenToHan(out);
		}
		// 指定バイト数でカットする
		if (cutByte != null) {
			try {
				int beforeLength = out.length();
				out = StringUtil.cutString(out, cutByte);
				int afterLength = out.length();
				// カットされたら後ろに文字を付加する
				if (beforeLength > afterLength) {
					out = out + "...";
				}
			} catch (Exception e) {
				// Exceptionが出たらカットしないで処理続行
			}
		}
		// タグを返す
		return out;
	}

	/**
	 * value属性のセット.
	 *
	 * @param value
	 *            値
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * escapeXml属性のセット.
	 *
	 * @param escapeXml
	 *            HTMLエスケープ処理するかどうかのフラグ
	 */
	public void setEscapeXml(boolean escapeXml) {
		this.escapeXml = escapeXml;
	}

	/**
	 * convertBR属性のセット.
	 *
	 * @param convertBR
	 *            改行コードをbrタグに変換するかどうかのフラグ
	 */
	public void setConvertBR(boolean convertBR) {
		this.convertBR = convertBR;
	}

	/**
	 * zenToHan属性のセット.
	 *
	 * @param zenToHan
	 *            全角を半角に変換するかどうかのフラグ
	 */
	public void setZenToHan(boolean zenToHan) {
		this.zenToHan = zenToHan;
	}

	/**
	 * cutByte属性のセット.
	 *
	 * @param cutByte
	 *            カットするバイト数
	 */
	public void setCutByte(Integer cutByte) {
		this.cutByte = cutByte;
	}
}
