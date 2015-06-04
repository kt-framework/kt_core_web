package jp.kt.web.device;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.kt.logger.ApplicationLogger;

/**
 * iPadクラス.
 *
 * @author tatsuya.kumon
 */
public class Ipad extends Pc {
	private static final long serialVersionUID = -5839178773256900577L;

	/**
	 * コンストラクタ.
	 *
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Ipad(String userAgent, ApplicationLogger logger) {
		super(userAgent, logger);
	}

	@Override
	void parseUserAgent(String userAgent) {
		// OS、ブラウザ、端末名で分割
		String os = null;
		String browser = null;
		String model = null;
		Pattern p1 = Pattern
				.compile("(.*\\(([a-zA-Z]+); U; CPU (.*); [a-z_\\-]+\\) AppleWebKit/[0-9\\.]+ \\([a-zA-Z, ]+\\)(.*))");
		Matcher m1 = p1.matcher(userAgent);
		if (m1.matches()) {
			// 機種名（iPhone or iPod）
			model = m1.group(2);
			// OS
			os = m1.group(3);
			// ブラウザ名
			browser = m1.group(4);
		}
		super.setInfo(os, browser, model);
	}
}
