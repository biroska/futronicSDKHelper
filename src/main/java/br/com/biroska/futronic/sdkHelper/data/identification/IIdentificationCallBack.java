package br.com.biroska.futronic.sdkHelper.data.identification;

import br.com.biroska.futronic.sdkHelper.callbacks.ICallBack;

/**
 * The interface specify identification call-back events which caller can receive.
 */
public interface IIdentificationCallBack  extends ICallBack
{
    /**
     * The "Get base template operation complete" event.
     *
     * @param bSuccess <code>true</code> if the operation succeeds, otherwise is <code>false</code>.
     * @param nResult The Futronic SDK return code.
     */
    public void OnGetBaseTemplateComplete( boolean bSuccess, int nResult );
}

