package jp.kt.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import jp.kt.fileio.FileUtil;
import jp.kt.tool.StringUtil;
import jp.kt.tool.Validator;

/**
 * マルチパートフォームの処理クラス.
 * <p>
 * ファイルアップロードを行う際に使用します.<br>
 * <br>
 * 【ポイント１】HTMLフォームがマルチパートフォームになってる必要があります.<br>
 * &nbsp;&nbsp;（例）&lt;form method="post" enctype="multipart/form-data"
 * action="xxx"&gt;<br>
 * <br>
 * 【ポイント２】受け取るためのServletクラスには、必ず@MultipartConfigアノテーションを付加すること.<br>
 * &nbsp;&nbsp;@MultipartConfigアノテーションの各種属性については下記の通り.<br>
 * &nbsp;&nbsp;◆location<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;ファイルを保存するディレクトリパスですが、設定ファイルなどへの外出しができないため、使用不可.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;{@link UploadFile#saveFile(FileUtil)} を使ってください.<br>
 * &nbsp;&nbsp;◆fileSizeThreshold<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;アップロードされたファイルがディスクに書き込まれるサイズのしきい値.<br>
 * &nbsp;&nbsp;◆maxFileSize<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;アップロードされるファイルの最大サイズ.<br>
 * &nbsp;&nbsp;◆maxRequestSize<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;multipart/form-dataリクエストの最大サイズ.<br>
 * <br>
 * 【ポイント３】ファイル以外の通常のパラメータは {@link HttpServletRequest#getParameter(String)}
 * で取得してください.<br>
 * </p>
 *
 * @author tatsuya.kumon
 */
public class MultipartForm {
	/** アップロードファイル情報のリスト */
	private List<UploadFile> uploadFileList;

	/**
	 * コンストラクタ.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @throws Exception
	 *             処理中に例外発生した場合
	 */
	public MultipartForm(HttpServletRequest req) throws Exception {
		this.uploadFileList = new ArrayList<>();
		for (Part part : req.getParts()) {
			if (part.getSize() > 0) {
				// fileでない、通常のパラメータはファイルサイズ0なので、0より大きいものをリストに追加
				this.uploadFileList.add(new UploadFile(part));
			}
		}
	}

	/**
	 * 指定したパラメータ名のアップロードファイルを取得する.
	 *
	 * @param name
	 *            パラメータ名
	 * @return アップロードしたファイル
	 */
	public List<UploadFile> getUploadFileList(String name) {

		List<UploadFile> list = new ArrayList<UploadFile>();
		if (name != null) {
			for (UploadFile f : this.uploadFileList) {
				if (f.getParamName().equals(name)) {
					// パラメータ名が合致すればリストにセットする
					list.add(f);
				}
			}
		}
		return list;
	}

	/**
	 * アップロードファイル情報管理クラス.
	 * <p>
	 * {@link Part}のラッピングクラス.
	 * </p>
	 *
	 * @author tatsuya.kumon
	 */
	public final class UploadFile implements Serializable {
		private Part part;

		/**
		 * コンストラクタ.
		 *
		 * @param part
		 *            {@link Part} オブジェクト
		 */
		private UploadFile(Part part) {
			this.part = part;
		}

		/**
		 * パラメータ名の取得.
		 *
		 * @return パラメータ名
		 */
		private String getParamName() {
			return part.getName();
		}

		/**
		 * ファイル内容の取得.
		 * <p>
		 * アップロードファイルのサイズが大きい場合はOutOfMemoryになる場合があるので、<br>
		 * ファイル保存が目的の場合は {@link UploadFile#saveFile(FileUtil)} を使用すること.
		 * </p>
		 *
		 * @return ファイル内容
		 * @throws IOException
		 *             入出力エラーが発生した場合
		 */
		public byte[] getContent() throws IOException {
			try (BufferedInputStream is = new BufferedInputStream(
					part.getInputStream());
					ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				copy(is, os);
				return os.toByteArray();
			}
		}

		/**
		 * アップロードファイルをファイルに保存する.
		 *
		 * @param outFile
		 *            出力ファイルの {@link FileUtil} オブジェクト
		 * @throws Exception
		 *             処理中に例外発生した場合
		 */
		public void saveFile(FileUtil outFile) throws Exception {
			try (BufferedInputStream is = new BufferedInputStream(
					part.getInputStream());
					BufferedOutputStream os = new BufferedOutputStream(
							new FileOutputStream(outFile.getPath()))) {
				copy(is, os);
			}
		}

		/**
		 * {@link InputStream} を {@link OutputStream} にコピーする.
		 *
		 * @param is
		 *            {@link InputStream}
		 * @param os
		 *            {@link OutputStream}
		 * @throws IOException
		 */
		private void copy(InputStream is, OutputStream os) throws IOException {
			// 入力ファイルを読み込み出力ストリームに書き込んでいく
			int ava = 0;
			while ((ava = is.available()) > 0) {
				byte[] bs = new byte[ava];
				is.read(bs);
				os.write(bs);
			}
		}

		/**
		 * ファイルサイズの取得.
		 *
		 * @return ファイルサイズ
		 */
		public long getSize() {
			return part.getSize();
		}

		/**
		 * アップロードファイル名を取得する.
		 *
		 * @return アップロードファイル名
		 */
		public String getFilename() {
			String filename = null;
			/*
			 * ヘッダ値 Content-Disposition からファイル名を取得
			 */
			for (String cd : part.getHeader("Content-Disposition").split(";")) {
				if (cd.trim().startsWith("filename")) {
					filename = cd.substring(cd.indexOf('=') + 1).trim()
							.replace("\"", "");
				}
			}
			/*
			 * ブラウザによってフルパスが取得される場合もあるので、ファイル名のみにする
			 */
			if (!Validator.isEmpty(filename)) {
				String path = StringUtil.replaceAll(filename, "\\", "/");
				int index = path.lastIndexOf('/');
				if (index < 0) {
					// スラッシュがなかった場合は全体がファイル名
					filename = path;
				} else {
					// スラッシュがあった場合はそれ以降がファイル名
					filename = path.substring(index + 1);
				}
			}
			return filename;
		}
	}
}
