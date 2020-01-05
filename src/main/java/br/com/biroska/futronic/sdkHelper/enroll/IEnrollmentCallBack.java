package br.com.biroska.futronic.sdkHelper.enroll;

import br.com.biroska.futronic.sdkHelper.callbacks.ICallBack;

/**
 * The interface specify enrollment call-back events which caller can receive.
 */
public interface IEnrollmentCallBack extends ICallBack
{
    /**
     * The "Enrollment operation complete" event.
     *
     * @param bSuccess <code>true</code> if the operation succeeds, otherwise is
     * <code>false</code>.
     * @param nResult The Futronic SDK return code (see FTRAPI.h).
     */
    public void OnEnrollmentComplete( boolean bSuccess, int nResult );
}

