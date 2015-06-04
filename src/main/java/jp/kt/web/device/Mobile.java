package jp.kt.web.device;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.kt.logger.ApplicationLogger;

/**
 * 携帯端末クラス.
 *
 * @author tatsuya.kumon
 */
public class Mobile extends Device {
	private static final long serialVersionUID = 7461824303551784449L;

	/** 携帯キャリア */
	private Carrier carrier;

	/** poor携帯フラグ */
	private boolean isPoor;

	/**
	 * コンストラクタ.
	 *
	 * @param userAgent
	 *            ユーザエージェント
	 * @param logger
	 *            {@link ApplicationLogger}オブジェクト
	 */
	Mobile(String userAgent, ApplicationLogger logger) {
		super(userAgent, logger);
	}

	/**
	 * ドコモであるか判定.
	 *
	 * @return ドコモの場合はtrue
	 */
	public boolean isDocomo() {
		return this.carrier.equals(Carrier.DOCOMO);
	}

	/**
	 * auであるか判定.
	 *
	 * @return auの場合はtrue
	 */
	public boolean isAu() {
		return this.carrier.equals(Carrier.AU);
	}

	/**
	 * ソフトバンクであるか判定.
	 *
	 * @return ソフトバンクの場合はtrue
	 */
	public boolean isSoftbank() {
		return this.carrier.equals(Carrier.SOFTBANK);
	}

	/**
	 * poor端末であるか判定.
	 *
	 * @return poor端末の場合はtrue
	 */
	public boolean isPoor() {
		return this.isPoor;
	}

	@Override
	void parseUserAgent(String userAgent) {
		String model = null;
		if (userAgent.startsWith("DoCoMo")) {
			/*
			 * Docomo
			 */
			if (userAgent.startsWith("DoCoMo/1.0/")) {
				/*
				 * iモードブラウザ1.0　poor
				 */
				// 先頭の「DoCoMo/1.0/」を除去
				model = userAgent.substring("DoCoMo/1.0/".length());
				int index = model.indexOf("/");
				if (index > 0) {
					// スラッシュが存在する場合は、その前の文字までを機種名とする
					model = model.substring(0, index);
				}
				// poorフラグをONにする
				this.isPoor = true;
			} else if (userAgent.startsWith("DoCoMo/2.0 ")) {
				/*
				 * iモードブラウザ2.0　rich
				 */
				// 先頭の「DoCoMo/2.0 」を除去
				model = userAgent.substring("DoCoMo/2.0 ".length());
				int index = model.indexOf("(");
				if (index > 0) {
					// 丸括弧が存在する場合は、その前の文字までを機種名とする
					model = model.substring(0, index);
				}
			}
			// キャリアをセット
			this.carrier = Carrier.DOCOMO;
		} else if (userAgent.startsWith("KDDI")) {
			/*
			 * au
			 */
			Pattern p = Pattern.compile("(KDDI-([a-zA-Z0-9]{4}) .+)");
			Matcher m = p.matcher(userAgent);
			if (m.matches()) {
				// 機種名
				model = m.group(2);
				// 機種名の3文字目が2だと第2世代なので、poor端末
				if (model.toCharArray()[2] == '2') {
					// poorフラグをONにする
					this.isPoor = true;
				}
			}
			// キャリアをセット
			this.carrier = Carrier.AU;
		} else if (userAgent.startsWith("J-PHONE")
				|| userAgent.startsWith("SoftBank")
				|| userAgent.startsWith("Vodafone")) {
			/*
			 * SoftBank
			 */
			Pattern p = Pattern
					.compile("([a-zA-Z\\-]+/[0-9\\.]+/([a-zA-Z0-9]+)/.+)");
			Matcher m = p.matcher(userAgent);
			if (m.matches()) {
				// 機種名
				model = m.group(2);
			}
			// 先頭がJ-PHONEだと、poor端末
			if (userAgent.startsWith("J-PHONE")) {
				// poorフラグをONにする
				this.isPoor = true;
			}
			// キャリアをセット
			this.carrier = Carrier.SOFTBANK;
		}
		// 機種名をセットする（OS、ブラウザは空）
		super.setInfo(null, null, model);
	}

	/**
	 * 携帯キャリア名を取得する.
	 *
	 * @return 携帯キャリア名
	 */
	public String getCarrier() {
		String result;
		if (this.carrier == null) {
			result = "";
		} else {
			result = this.carrier.getCarrierName();
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		builder.append(" [carrier=");
		builder.append(this.carrier == null ? this.carrier : this.carrier
				.getCarrierName());
		builder.append(", model=");
		builder.append(getModel());
		builder.append(", isPoor=");
		builder.append(this.isPoor);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 携帯キャリア定義クラス.
	 *
	 * @author tatsuya.kumon
	 */
	private static class Carrier implements Serializable {
		private static final long serialVersionUID = 1L;

		/** ドコモ */
		static final Carrier DOCOMO = new Carrier("DoCoMo");

		/** au */
		static final Carrier AU = new Carrier("au");

		/** ソフトバンク */
		static final Carrier SOFTBANK = new Carrier("SoftBank");

		/** キャリア名称 */
		private String carrierName;

		/**
		 * 内部コンストラクタ.
		 *
		 * @param carrierName
		 *            キャリア名称
		 */
		private Carrier(String carrierName) {
			this.carrierName = carrierName;
		}

		/**
		 * キャリア名称を返す.
		 *
		 * @return キャリア名称
		 */
		public String getCarrierName() {
			return this.carrierName;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = false;
			if (obj instanceof Carrier) {
				Carrier c = (Carrier) obj;
				if (c.carrierName.equals(this.carrierName)) {
					result = true;
				}
			}
			return result;
		}
	}
}
