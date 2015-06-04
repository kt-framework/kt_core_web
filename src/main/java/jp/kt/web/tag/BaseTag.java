package jp.kt.web.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import jp.kt.web.device.Device;

/**
 * 全カスタムタグの基底クラス.
 * 
 * @author tatsuya.kumon
 */
abstract class BaseTag extends BodyTagSupport {
	@Override
	public int doEndTag() throws JspException {
		String tagText = createTag();
		write(tagText);
		return EVAL_PAGE;
	}

	/**
	 * タグ作成.
	 * 
	 * @return タグテキスト
	 */
	protected abstract String createTag();

	/**
	 * タグ出力.
	 * 
	 * @param text
	 *            タグテキスト
	 * @throws JspException
	 */
	private void write(String text) throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			writer.print(text);
		} catch (IOException e) {
			throw new JspException(e.getMessage());
		}
	}

	/**
	 * アクセス端末区分を取得する.
	 * 
	 * @return {@link Device}オブジェクト
	 */
	protected Device getDevice() {
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		return Device.getInstance(req, null);
	}
}
