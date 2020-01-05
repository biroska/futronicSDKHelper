package br.com.biroska.futronic.sdkHelper.exception;

/**
 * Represent errors that occur during SDK API functions execution.
 */
public class FutronicException extends java.lang.Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3806370383853083359L;

	/**
	 * Creates a new instance of <code>FutronicException</code> with the specified
	 * error code.
	 *
	 * @param nErrorCode Error code
	 */
	public FutronicException(int nErrorCode) {
		m_ErrorCode = nErrorCode;
	}

	/**
	 * Constructs an instance of <code>FutronicException</code> with the specified
	 * error code and with the specified detail message.
	 *
	 * @param nErrorCode Error code
	 * @param msg        the detail message.
	 */
	public FutronicException(int nErrorCode, String msg) {
		super(msg);
		m_ErrorCode = nErrorCode;
	}

	/**
	 * Gets a error code.
	 */
	public int getErrorCode() {
		return m_ErrorCode;
	}

	private int m_ErrorCode;
}
