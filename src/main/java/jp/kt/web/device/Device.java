package jp.kt.web.device;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import jp.kt.logger.ApplicationLogger;
import jp.kt.tool.Validator;
import jp.kt.web.RequestHeader;

/**
 * 全端末区分の基底クラス.
 * 
 * @author tatsuya.kumon
 */
public abstract class Device implements Serializable {
	private static final long serialVersionUID = 2587277089087906687L;

	/** OS */
	private String os;

	/** ブラウザ */
	private String browser;

	/** 機種 */
	private String model;

	/**
	 * コンストラクタ.
	 * 
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Device(String userAgent, ApplicationLogger logger) {
		try {
			// ユーザエージェントを解析して、OS、機種名、ブラウザ名をセットする
			this.parseUserAgent(userAgent);
		} catch (Throwable e) {
			// ユーザエージェント解析でエラーが発生した場合は、スルーする（想定外のユーザエージェントが来る可能性があるため）
			logger.errorLog("A040", "ユーザエージェントの解析に失敗しました [" + userAgent + "]",
					e);
		}
	}

	/**
	 * ユーザエージェントから{@link Device}オブジェクトを生成する.
	 * 
	 * @param req
	 *            HTTPリクエスト
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 * @return {@link Device}オブジェクト
	 */
	public static Device getInstance(HttpServletRequest req,
			ApplicationLogger logger) {
		return getInstance(RequestHeader.getUserAgent(req), logger);
	}

	/**
	 * ユーザエージェントから{@link Device}オブジェクトを生成する.
	 * 
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 * @return {@link Device}オブジェクト
	 */
	public static final Device getInstance(String userAgent,
			ApplicationLogger logger) {
		if (logger == null) {
			logger = ApplicationLogger.NULL_LOGGER;
		}
		Device device = null;
		if (!Validator.isEmpty(userAgent)) {
			if (userAgent.startsWith("DoCoMo") || userAgent.startsWith("KDDI")
					|| userAgent.startsWith("J-PHONE")
					|| userAgent.startsWith("SoftBank")
					|| userAgent.startsWith("Vodafone")) {
				/*
				 * 携帯
				 */
				device = new Mobile(userAgent, logger);
			} else if (userAgent.matches(".*iPhone;.*")
					|| userAgent.matches(".*iPod;.*")) {
				/*
				 * iPhone
				 */
				device = new Iphone(userAgent, logger);
			} else if (userAgent.matches(".*iPad;.*")) {
				/*
				 * iPad
				 */
				device = new Ipad(userAgent, logger);
			} else if (userAgent.matches(".*Android.*")) {
				/*
				 * Android
				 */
				device = new Android(userAgent, logger);
			} else if (userAgent.matches(".*WILLCOM;.*")
					|| userAgent.matches(".*DDIPOCKET;.*")) {
				/*
				 * PHS
				 */
				device = new Phs(userAgent, logger);
			}
		}
		if (device == null) {
			/*
			 * その他は全てPC
			 */
			device = new Pc(userAgent, logger);
		}
		return device;
	}

	/**
	 * ユーザエージェント解析して、各属性にセットする.
	 * 
	 * @param userAgent
	 *            ユーザエージェント
	 */
	abstract void parseUserAgent(String userAgent);

	/**
	 * OS、ブラウザ、機種の各情報をセットする.
	 * 
	 * @param os
	 *            OS情報
	 * @param browser
	 *            ブラウザ情報
	 * @param model
	 *            機種情報
	 */
	void setInfo(String os, String browser, String model) {
		if (os != null) {
			this.os = os.trim();
		}
		if (browser != null) {
			this.browser = browser.trim();
		}
		if (model != null) {
			this.model = model.trim();
		}
	}

	/**
	 * OS情報を取得する.
	 * 
	 * @return OS情報
	 */
	public String getOs() {
		if (os == null) {
			return "";
		}
		return os;
	}

	/**
	 * ブラウザ情報を取得する
	 * 
	 * @return ブラウザ情報
	 */
	public String getBrowser() {
		if (browser == null) {
			return "";
		}
		return browser;
	}

	/**
	 * 機種情報を取得する.
	 * 
	 * @return 機種情報
	 */
	public String getModel() {
		if (model == null) {
			return "";
		}
		return model;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		builder.append(" [os=");
		builder.append(getOs());
		builder.append(", model=");
		builder.append(getModel());
		builder.append(", browser=");
		builder.append(getBrowser());
		builder.append("]");
		return builder.toString();
	}
}
