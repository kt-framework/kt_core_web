package jp.kt.web.servlet;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.kt.db.DbConnectManager;
import jp.kt.exception.KtException;
import jp.kt.exception.KtHttpException;
import jp.kt.exception.KtWarningException;
import jp.kt.logger.ApplicationLogger;
import jp.kt.prop.KtProperties;
import jp.kt.web.RequestHeader;
import jp.kt.web.device.Device;
import jp.kt.web.page.DownloadPage;
import jp.kt.web.page.ForwardPage;
import jp.kt.web.page.HttpCodePage;
import jp.kt.web.page.Page;
import jp.kt.web.page.RedirectPage;

/**
 * 全ての基底Servlet.
 *
 * @author tatsuya.kumon
 */
public abstract class BaseServlet extends HttpServlet {
	/** ログ出力クラス */
	private ApplicationLogger logger;

	/** サーバのホスト名 */
	private static String serverHostName;

	/** 共通エラーページ */
	private static final String ERROR_PAGE;

	/** 整数値のフォーマット */
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

	static {
		ERROR_PAGE = KtProperties.getInstance().getString(
				"kt.core.web.errorpage.path");
	}

	@Override
	public final void init() throws ServletException {
	}

	@Override
	public final void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Page page = null;
		Connection con = null;
		long start = System.nanoTime();
		try {
			// requestされる値の文字コードのセット
			req.setCharacterEncoding(KtProperties.getInstance()
					.getDefaultCharset());
			// logger生成
			String loggerName = getLoggerName(req);
			if (logger == null) {
				logger = new ApplicationLogger(loggerName, this.getClass());
			}
			// サーバのホスト名を取得
			try {
				if (serverHostName == null) {
					serverHostName = InetAddress.getLocalHost().getHostName();
				}
			} catch (Exception e) {
				serverHostName = "";
				logger.errorLog("A062", "サーバのホスト名取得に失敗しました", e);
			}
			// クライアント情報とアクセスしてきたURLをログ出力
			logger.infoLog("A001", createClientInfoText(req));
			// メモリ状況をログ出力
			logger.infoLog("A057", createMemoryStatusText());
			// HTTPメソッド制限チェック
			boolean isMethodError = true;
			for (HttpMethod hm : this.getPermitHttpMethods()) {
				if (hm.toString().equalsIgnoreCase(req.getMethod())) {
					// OK
					isMethodError = false;
					break;
				}
			}
			if (isMethodError) {
				throw new KtWarningException("A002", req.getMethod()
						+ "リクエストは許可していません");
			}
			// DBコネクション作成
			if (useDbConnection()) {
				con = DbConnectManager.createConnection(getDbJndiName());
			}
			// IPアドレス制限チェック
			checkIp(req, con);
			// ログインチェック
			if (isRequiredLogin() && !isLogin(req, res)) {
				// 要ログインなのにログインしていなければ指定のページへ遷移する
				page = getPageForNotLogin(req, res);
			} else {
				// ログイン不要、もしくは要ログインでログインしている場合は、
				// メイン処理実行
				page = execute(req, res, con);
			}
			// DBトランザクションのコミット
			if (useDbConnection()) {
				con.commit();
			}
		} catch (KtHttpException e) {
			// HTTPレスポンスコードを直接レスポンス
			page = new HttpCodePage(e.getHttpResponseCode());
		} catch (KtException e) {
			// 共通エラー画面をレスポンス
			page = exceptionOperation(e.getCode(), e, req, con);
		} catch (SQLException e) {
			// SQLException発生
			page = exceptionOperation("A003", e, req, con);
		} catch (Exception e) {
			// その他のException発生
			page = exceptionOperation("A004", e, req, con);
		} catch (Error e) {
			// java.lang.Error発生
			page = exceptionOperation("A005", e, req, con);
		} finally {
			// DBコネクションのclose
			try {
				if (useDbConnection() && con != null && !con.isClosed()) {
					con.close();
				}
			} catch (SQLException e2) {
				logger.errorLog("A008", e2.getMessage(), e2);
			}
		}
		// ページ遷移
		try {
			// 遷移実行
			if (page instanceof DownloadPage) {
				// ダウンロードの場合
				DownloadPage dPage = (DownloadPage) page;
				// ダウンロード実行
				dPage.download(res);
				// ページ遷移ログ
				outputPageLog("response " + page.getClass().getName());
			} else if (page instanceof ForwardPage) {
				// 遷移先URLを取得
				String url = ((ForwardPage) page).getUrl();
				// forwardの場合
				ServletContext sc = getServletContext();
				RequestDispatcher rd = sc.getRequestDispatcher(url);
				rd.forward(req, res);
				// ページ遷移ログ
				outputPageLog("forward to " + url);
			} else if (page instanceof RedirectPage) {
				// 遷移先URLを取得
				String url = ((RedirectPage) page).getUrl();
				if (url.startsWith("/")) {
					url = req.getContextPath() + url;
				}
				// redirectの場合
				res.sendRedirect(url);
				// ページ遷移ログ
				outputPageLog("redirect to " + url);
			} else if (page instanceof HttpCodePage) {
				// 直接HTTPレスポンスコードを返す場合
				int httpCode = ((HttpCodePage) page).getHttpResponseCode();
				res.sendError(httpCode);
				outputPageLog("response HTTP_ERROR:" + httpCode);
			}
		} catch (SocketException e) {
			// SocketExceptionはWARNでログ出力する
			logger.warnLog("A009", e.getMessage(), e);
		} catch (IOException e) {
			String className = e.getClass().getName();
			if (className
					.equals("org.apache.catalina.connector.ClientAbortException")) {
				// ClientAbortExceptionはWARNでログ出力する
				logger.warnLog("A009", e.getMessage(), e);
			} else {
				// それ以外のIOExceptionはERRORでログ出力する
				logger.errorLog("A010", e.getMessage(), e);
			}
		} catch (Exception e) {
			// ログ出力
			logger.errorLog("A010", e.getMessage(), e);
		} catch (Error e) {
			// ログ出力
			logger.errorLog("A011", e.getMessage(), e);
		}
		// 終了ログ出力
		StringBuilder endlog = new StringBuilder();
		endlog.append("[");
		endlog.append(getSessionId(req));
		endlog.append("] BaseServlet end");
		// 処理時間計測（ナノ秒）
		long nanosec = System.nanoTime() - start;
		// ミリ秒に変換
		long millisec = TimeUnit.NANOSECONDS.toMillis(nanosec);
		// 3桁区切りにフォーマット
		String timeStr = new DecimalFormat().format(millisec);
		endlog.append(" [処理時間:");
		endlog.append(timeStr);
		endlog.append("ms]");
		logger.infoLog("A039", endlog.toString());
	}

	/**
	 * サーバのホスト名を返す.
	 *
	 * @return サーバのホスト名.<br>
	 *         取得できなかった場合は空文字.
	 */
	protected final String getServerHostName() {
		return serverHostName;
	}

	/**
	 * ページ遷移ログ出力.
	 *
	 * @param message
	 *            出力メッセージ
	 */
	private void outputPageLog(String message) {
		if (isOutputPageLog()) {
			logger.infoLog("A042", message);
		}
	}

	/**
	 * ページ遷移先をログ出力するかの判定.
	 * <p>
	 * デフォルトでは出力する.<br>
	 * 出力したくない場合は、falseを返すメソッドでオーバーライドすること.
	 *
	 * @return 出力する場合はtrue
	 */
	protected boolean isOutputPageLog() {
		return true;
	}

	/**
	 * Exception発生時の共通処理.
	 *
	 * @param errorCode
	 *            エラーコード
	 * @param e
	 *            {@link Throwable}オブジェクト
	 * @param req
	 *            HTTPリクエスト
	 * @param con
	 *            DB接続
	 * @return エラー画面の {@link Page}オブジェクト
	 */
	private Page exceptionOperation(String errorCode, Throwable e,
			HttpServletRequest req, Connection con) {
		// リクエストに対するユニークキーを出力
		String sessionId = getSessionId(req);
		// ログ出力
		if (e instanceof KtWarningException || e instanceof SocketException) {
			// WarningException、SocketExceptionなら
			// WARNINGレベルでログ出力
			logger.warnLog(errorCode, "[" + sessionId + "] " + e.getMessage(),
					e);
		} else {
			// それ以外のExceptionならERRORレベルでログ出力
			logger.errorLog(errorCode, "[" + sessionId + "] " + e.getMessage(),
					e);
		}
		Page page = null;
		try {
			// エラーページ遷移のPage取得
			page = createErrorPage(errorCode, e, req);
			// DBトランザクションのロールバック
			if (useDbConnection() && con != null && !con.isClosed()) {
				con.rollback();
			}
		} catch (Exception e2) {
			logger.errorLog("A007", "[" + sessionId + "] " + e2.getMessage(),
					e2);
		}
		return page;
	}

	/**
	 * エラーページ遷移用のPage作成.
	 * <p>
	 * 各Servletは必要に応じてこのメソッドをオーバーライドすること.
	 *
	 * @param errorCode
	 *            エラーコード
	 * @param e
	 *            {@link Throwable}オブジェクト
	 * @param req
	 *            HTTPリクエスト
	 * @return エラー画面の {@link Page}オブジェクト
	 * @throws URISyntaxException
	 *             URIの書式が誤っている場合
	 */
	protected Page createErrorPage(String errorCode, Throwable e,
			HttpServletRequest req) throws URISyntaxException {
		// 原因Throwableも含めてListにする
		List<Throwable> tList = new ArrayList<Throwable>();
		while (true) {
			tList.add(e);
			e = e.getCause();
			if (e == null) {
				break;
			}
		}
		// 遷移先の指定
		Page page = new ForwardPage(ERROR_PAGE);
		// エラーコード、エラーメッセージ、現在日時をrequest属性にセット
		req.setAttribute("errorCode", errorCode);
		req.setAttribute("throwableList", tList);
		req.setAttribute("nowDatetime", new Date());
		return page;
	}

	/**
	 * 許可するHTTPメソッドを返す.
	 * <p>
	 * デフォルトは全メソッド使用可となっているため、制限したい場合はオーバーライドする.
	 *
	 * @return 許可するHTTPメソッド群
	 */
	protected HttpMethod[] getPermitHttpMethods() {
		// デフォルトは全メソッド使用可
		return HttpMethod.values();
	}

	/**
	 * DB接続する必要があるServletかどうかを返す.
	 *
	 * @return DB接続しない場合はfalse、DB接続する場合はtrue.
	 */
	protected abstract boolean useDbConnection();

	/**
	 * DB接続先のJNDI名を取得する.
	 * <p>
	 * context.xmlなどに記載したJNDI名.
	 *
	 * @return DB接続先のJNDI名
	 */
	protected abstract String getDbJndiName();

	/**
	 * ログインが必要なServletかどうかを返す.
	 *
	 * @return ログイン不要の場合はfalse、要ログインの場合はtrue.
	 */
	protected abstract boolean isRequiredLogin();

	/**
	 * ApplicationLoggerオブジェクトを取得.
	 *
	 * @return {@link ApplicationLogger}オブジェクト
	 */
	protected final ApplicationLogger getLogger() {
		return logger;
	}

	/**
	 * ログ出力のためのlogger名.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @return logger名
	 */
	protected abstract String getLoggerName(HttpServletRequest req);

	/**
	 * メイン処理.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @param res
	 *            HTTPレスポンス
	 * @param con
	 *            DB接続
	 * @return 遷移ページを示す {@link Page}オブジェクト
	 * @throws Exception
	 *             処理中に例外発生した場合
	 */
	protected abstract Page execute(HttpServletRequest req,
			HttpServletResponse res, Connection con) throws Exception;

	/**
	 * ログイン判定.
	 * <p>
	 * デフォルトは固定でtrueを返す.<br>
	 * 必要に応じてオーバーライドすること.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @param res
	 *            HTTPレスポンス
	 * @return ログイン状態の場合はtrue
	 * @throws Exception
	 *             処理中に例外発生した場合
	 */
	protected boolean isLogin(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		return true;
	}

	/**
	 * 要ログインなのにログインしていない場合の遷移先を返す.
	 * <p>
	 * {@link BaseServlet#isLogin(HttpServletRequest, HttpServletResponse)}
	 * とセットでオーバーライドして使用すること.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @param res
	 *            HTTPレスポンス
	 * @return 遷移先の {@link Page}オブジェクト
	 * @throws Exception
	 *             処理中に例外発生した場合
	 */
	protected Page getPageForNotLogin(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return null;
	}

	/**
	 * セッションIDを取得する.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @return 現在のセッションのセッションID
	 */
	protected final String getSessionId(HttpServletRequest req) {
		return req.getSession().getId();
	}

	/**
	 * IP制限チェック.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @param con
	 *            DB接続
	 * @throws Exception
	 */
	private void checkIp(HttpServletRequest req, Connection con)
			throws Exception {
		// 許可されたIPアドレス群が1件以上ある場合のみチェックする
		List<String> ipList = getPermitIpList(req, con);
		if (ipList != null && ipList.size() >= 1) {
			// 接続元IPアドレス
			String ipAddress = getClientIpAddress(req);
			// 許可されたIPアドレスで無い場合は、Exception
			if (!ipList.contains(ipAddress)) {
				throw new KtException("B038", "ご利用のIPアドレスからは接続が許可されていません ["
						+ ipAddress + "]");
			}
		}
	}

	/**
	 * 許可されたIPアドレス群を取得する.
	 * <p>
	 * IP制限する場合のみオーバーライドする.<br>
	 * デフォルトはnullを返す.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @param con
	 *            DB接続
	 * @return IPアドレスのList
	 * @throws Exception
	 *             処理中に例外発生した場合
	 */
	protected List<String> getPermitIpList(HttpServletRequest req,
			Connection con) throws Exception {
		return null;
	}

	/**
	 * web.xmlからコンテキストパラメータ（context-paramタグの値）を取得する.
	 *
	 * @param name
	 *            コンテキストパラメータ名
	 * @return コンテキストパラメータ値
	 */
	protected String getContextParameter(String name) {
		ServletContext sc = getServletConfig().getServletContext();
		return sc.getInitParameter(name);
	}

	/**
	 * 端末情報などをログ出力用文字列として作成する.
	 * <p>
	 * 1:端末区分（PC、poor携帯、rich携帯）<br>
	 * 2:ユーザエージェント<br>
	 * 3:auサブスクライブ番号（無ければ出さない）<br>
	 * 4:アクセスホスト<br>
	 * 5:アクセスURI（クエリストリング含む）<br>
	 * 6:リファラー
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @return クライアント情報のテキスト
	 */
	private String createClientInfoText(HttpServletRequest req) {
		StringBuilder log = new StringBuilder();
		// セッションIDを出力
		log.append("[");
		log.append(getSessionId(req));
		log.append("] ");
		// 端末区分を出力
		log.append("[type]");
		Device device = Device.getInstance(req, logger);
		log.append(device.getClass().getSimpleName());
		log.append(" ");
		// アクセスしてきたメソッドを出力
		log.append("[method]");
		log.append(RequestHeader.getMethod(req));
		log.append(" ");
		// アクセスしてきたホストを出力
		log.append("[host]");
		log.append(RequestHeader.getScheme(req));
		log.append("://");
		log.append(RequestHeader.getHost(req));
		log.append(" ");
		// アクセスしてきたURLを出力
		log.append("[uri]");
		log.append(RequestHeader.getUri(req));
		log.append(" ");
		// リファラーを出力
		log.append("[ref]");
		log.append(RequestHeader.getReferer(req));
		log.append(" ");
		// ユーザエージェントを出力
		log.append("[ua]");
		log.append(RequestHeader.getUserAgent(req));
		log.append(" ");
		// 接続元IPを出力
		log.append("[IP]");
		log.append(getClientIpAddress(req));
		return log.toString();
	}

	/**
	 * ヒープメモリの使用状況をログ出力するためのメッセージ生成.
	 *
	 * @return ログ出力用のメッセージ
	 */
	private String createMemoryStatusText() {
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = mbean.getHeapMemoryUsage();
		long init = heapUsage.getInit();
		long used = heapUsage.getUsed();
		long committed = heapUsage.getCommitted();
		long max = heapUsage.getMax();
		StringBuilder log = new StringBuilder();
		log.append("[");
		log.append(serverHostName);
		log.append("] ");
		log.append("メモリ状況 [init=");
		log.append(DECIMAL_FORMAT.format(init));
		log.append("] [used=");
		log.append(DECIMAL_FORMAT.format(used));
		log.append("(");
		log.append(used * 100 / max);
		log.append("%)] [committed=");
		log.append(DECIMAL_FORMAT.format(committed));
		log.append("(");
		log.append(committed * 100 / max);
		log.append("%)] [max=");
		log.append(DECIMAL_FORMAT.format(max));
		log.append("]");
		return log.toString();
	}

	/**
	 * クライアントのIPアドレスを取得する.
	 * <p>
	 * プロキシを使用しているなどサーバ環境によって<br>
	 * 取得方法が異なるので、それぞれ実装してください.
	 *
	 * @param req
	 *            HTTPリクエスト
	 * @return クライアントのIPアドレス
	 */
	protected abstract String getClientIpAddress(HttpServletRequest req);
}
