package jp.kt.web.page;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jp.kt.tool.Validator;

/**
 * ダウンロードの基底クラス.
 *
 * @author tatsuya.kumon
 */
public abstract class DownloadPage implements Page {
	/** コンテントタイプ */
	private String contentType;

	/** ダウンロードファイル名 */
	private String downloadFileName;

	/** ダウンロードデータ */
	private byte[] downloadData;

	/**
	 * コンテントタイプを明示的にセット.
	 * <p>
	 * このメソッドを呼ばなければデフォルトのコンテントタイプが採用されます.
	 * </p>
	 *
	 * @param contentType
	 *            コンテントタイプ
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * ダウンロードファイル名を指定する.
	 * <p>
	 * これを指定すると、コンテントタイプに関わらずダウンロードダイアログが出てしまうので注意すること.
	 * </p>
	 *
	 * @param downloadFileName
	 *            ダウンロードファイル名
	 */
	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}

	/**
	 * デフォルトコンテントタイプの取得.
	 *
	 * @return デフォルトコンテントタイプ
	 */
	abstract String getDefaultContentType();

	/**
	 * コンテントタイプ取得.
	 *
	 * @return コンテントタイプ
	 */
	final String getContentType() {
		String result;
		if (Validator.isEmpty(this.contentType)) {
			// 明示的に指定されていなければデフォルトのコンテントタイプを返す
			result = getDefaultContentType();
		} else {
			result = this.contentType;
		}
		return result;
	}

	/**
	 * ダウンロードデータのセット.
	 *
	 * @param downloadData
	 *            ダウンロードするbyte配列
	 */
	void setDownloadData(byte[] downloadData) {
		this.downloadData = downloadData;
	}

	/**
	 * ダウンロード実行.<br>
	 * このメソッドはBaseServletからのみ実行可.
	 *
	 * @param res
	 *            HttpServletResponse
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public final void download(HttpServletResponse res) throws IOException {
		// コンテントタイプ設定
		if (!Validator.isEmpty(getContentType())) {
			res.setContentType(getContentType());
		}
		// ダウンロードファイル名設定
		if (!Validator.isEmpty(downloadFileName)) {
			res.setHeader("Content-Disposition", "attachment; filename=\""
					+ downloadFileName + "\"");
		}
		// ダウンロード処理
		ByteArrayInputStream bais = new ByteArrayInputStream(this.downloadData);
		ServletOutputStream sos = null;
		try {
			// レスポンスするOutputStream
			sos = res.getOutputStream();
			// 入力＆出力
			byte[] buffer = new byte[1024];
			int readByteSize;
			while ((readByteSize = bais.read(buffer)) != -1) {
				sos.write(buffer, 0, readByteSize);
			}
		} finally {
			if (sos != null)
				sos.close();
			if (bais != null)
				bais.close();
		}
	}
}
