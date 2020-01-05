package br.com.biroska.futronic.sdkHelper.data.verification;

import br.com.biroska.futronic.sdkHelper.callbacks.ICallBack;

/**
 * The interface specify verification call-back events which caller can receive.
 */
public interface IVerificationCallBack extends ICallBack
{
    /**
     * The "Verification operation complete" event.
     *
     * @param bSuccess <code>true</code> if the operation succeeds, otherwise is <code>false</code>
     * @param nResult the Futronic SDK return code.
     * @param bVerificationSuccess if the operation succeeds (bSuccess is <code>true</code>), 
     * this parameters shows the verification operation result. <code>true</code>
     * if the captured from the attached scanner template is matched, otherwise is <code>false</code>.
     */
    public void OnVerificationComplete( boolean bSuccess,
                                        int nResult,
                                        boolean bVerificationSuccess );
}
