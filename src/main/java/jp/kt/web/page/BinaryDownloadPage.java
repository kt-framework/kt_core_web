package jp.kt.web.page;

import java.io.IOException;

import jp.kt.internet.MimeType;

/**
 * バイナリデータレスポンス用のPage実装クラス.
 *
 * @author tatsuya.kumon
 */
public class BinaryDownloadPage extends DownloadPage {
	/**
	 * コンストラクタ.
	 *
	 * @param responseData
	 *            レスポンスするバイトデータ
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public BinaryDownloadPage(byte[] responseData) throws IOException {
		super.setDownloadData(responseData);
	}

	@Override
	String getDefaultContentType() {
		return MimeType.getMimeType("bin");
	}
}
