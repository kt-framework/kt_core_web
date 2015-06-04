package jp.kt.web.device;

import jp.kt.logger.ApplicationLogger;

/**
 * PC端末.
 *
 * @author tatsuya.kumon
 */
public class Pc extends Device {
	private static final long serialVersionUID = 8306859751212102376L;

	/**
	 * コンストラクタ.
	 *
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Pc(String userAgent, ApplicationLogger logger) {
		super(userAgent, logger);
	}

	@Override
	void parseUserAgent(String userAgent) {
	}
}
