package br.com.biroska.futronic.sdkHelper.callbacks;

import br.com.biroska.futronic.sdkHelper.data.proccess.FTR_PROGRESS;

/**
 * The interface specify common call-back events which caller can receive.
 */
public interface ICallBack
{
    /**
     * The "Put your finger on the scanner" event.
     *
     * @param Progress the current progress data structure.
     */
    public void OnPutOn( FTR_PROGRESS Progress );

    /**
     * The "Take off your finger from the scanner" event.
     *
     * @param Progress the current progress data structure.
     */
    public void OnTakeOff( FTR_PROGRESS Progress );

    /**
     * The "Show the current fingerprint image" event.
     *
     * @param Bitmap the instance of Bitmap class with fingerprint image.
     */
    public void UpdateScreenImage( java.awt.image.BufferedImage Bitmap );

    /**
     * The "Fake finger detected" event.
     *
     * @param Progress the fingerprint image.
     *
     * @return <code>true</code> if the current indetntification operation 
     * should be aborted, otherwise is <code>false</code>
     */
    public boolean OnFakeSource( FTR_PROGRESS Progress );
}
