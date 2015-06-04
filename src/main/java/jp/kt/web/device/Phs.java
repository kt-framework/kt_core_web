package jp.kt.web.device;

import jp.kt.logger.ApplicationLogger;

/**
 * PHS端末.
 *
 * @author tatsuya.kumon
 */
public class Phs extends Device {
	private static final long serialVersionUID = 3171478642081574555L;

	/**
	 * コンストラクタ.
	 *
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Phs(String userAgent, ApplicationLogger logger) {
		super(userAgent, logger);
	}

	@Override
	void parseUserAgent(String userAgent) {
	}
}
