package jp.kt.web.device;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.kt.logger.ApplicationLogger;
import jp.kt.tool.Validator;

/**
 * Andorid端末クラス.
 * 
 * @author tatsuya.kumon
 */
public class Android extends Pc {
	private static final long serialVersionUID = 1366827011257935711L;

	/**
	 * コンストラクタ.
	 * 
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Android(String userAgent, ApplicationLogger logger) {
		super(userAgent, logger);
	}

	@Override
	final void parseUserAgent(String userAgent) {
		// OS、ブラウザ、端末名で分割
		String os = null;
		String browser = null;
		String model = null;
		/*
		 * 標準ブラウザであるSafariでマッチング
		 */
		Pattern p1 = Pattern
				.compile("(.*(Android [^;]*); [a-z_\\-]*;?(.*) Build/(.*))");
		Matcher m1 = p1.matcher(userAgent);
		if (m1.matches()) {
			// OS
			os = m1.group(2);
			// 機種名
			model = m1.group(3);
			// ブラウザ
			browser = m1.group(4);
			if (!Validator.isEmpty(browser)) {
				Pattern p2 = Pattern.compile("(.* (Version/.*))");
				Matcher m2 = p2.matcher(userAgent);
				if (m2.matches()) {
					browser = m2.group(2);
				} else {
					browser = null;
				}
			}
		}
		super.setInfo(os, browser, model);
	}
}
