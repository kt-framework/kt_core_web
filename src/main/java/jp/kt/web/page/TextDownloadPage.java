package jp.kt.web.page;

import java.io.UnsupportedEncodingException;

import jp.kt.internet.MimeType;
import jp.kt.prop.KtProperties;

/**
 * テキストデータレスポンス用のPage実装クラス.
 *
 * @author tatsuya.kumon
 */
public class TextDownloadPage extends DownloadPage {
	/**
	 * コンストラクタ.
	 * <p>
	 * デフォルト文字コードでの処理となります.
	 * </p>
	 *
	 * @param responseText
	 *            レスポンスするテキスト
	 * @throws UnsupportedEncodingException
	 *             指定されたエンコーディングがサポートされていない場合
	 */
	public TextDownloadPage(String responseText)
			throws UnsupportedEncodingException {
		this(responseText, KtProperties.getInstance().getDefaultCharset());
	}

	/**
	 * コンストラクタ.
	 *
	 * @param responseText
	 *            レスポンスするテキスト
	 * @param charset
	 *            文字コード
	 * @throws UnsupportedEncodingException
	 *             指定されたエンコーディングがサポートされていない場合
	 */
	public TextDownloadPage(String responseText, String charset)
			throws UnsupportedEncodingException {
		// バイト配列に変換
		super.setDownloadData(responseText.getBytes(charset));
	}

	@Override
	String getDefaultContentType() {
		return MimeType.getMimeType("html");
	}
}
